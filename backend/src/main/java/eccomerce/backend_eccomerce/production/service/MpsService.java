package eccomerce.backend_eccomerce.production.service;

import eccomerce.backend_eccomerce.common.constants.RoleConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.engineering.entity.ProductEntity;
import eccomerce.backend_eccomerce.engineering.repository.ProductRepository;
import eccomerce.backend_eccomerce.production.dto.GenerateMpsDto;
import eccomerce.backend_eccomerce.production.dto.MpsRecordDto;
import eccomerce.backend_eccomerce.production.dto.UpdateMpsRecordDto;
import eccomerce.backend_eccomerce.production.entity.MpsRecordEntity;
import eccomerce.backend_eccomerce.production.enums.MpsPeriodMode;
import eccomerce.backend_eccomerce.production.enums.DemandStatus;
import eccomerce.backend_eccomerce.production.enums.DemandType;
import eccomerce.backend_eccomerce.production.repository.DemandRepository;
import eccomerce.backend_eccomerce.production.repository.MpsRecordRepository;
import eccomerce.backend_eccomerce.users.entity.UserEntity;
import eccomerce.backend_eccomerce.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MpsService {
    @Autowired
    private MpsRecordRepository mpsRecordRepository;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseMessage<List<MpsRecordDto>> findAll(UUID productId, MpsPeriodMode periodMode) {
        try {
            UserEntity currentUser = resolveCurrentAdmin();
            UUID currentEnterpriseId = currentUser.enterprise.getId();
            ProductEntity product = resolveProduct(productId, currentEnterpriseId);
            MpsPeriodMode effectiveMode = periodMode == null ? MpsPeriodMode.WEEKLY : periodMode;
            List<MpsRecordDto> records = mpsRecordRepository.findByEnterpriseIdAndProductIdAndPeriodModeOrderByPeriodStartDateAsc(currentEnterpriseId, product.getId(), effectiveMode)
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseMessage.success(records, "Plan maestro obtenido correctamente", records.size());
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo obtener el plan maestro", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo obtener el plan maestro", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<List<MpsRecordDto>> generate(GenerateMpsDto dto) {
        try {
            UserEntity currentUser = resolveCurrentAdmin();
            validateRequestedEnterprise(dto.enterpriseId, currentUser.enterprise.getId());
            UUID currentEnterpriseId = currentUser.enterprise.getId();
            ProductEntity product = resolveProduct(dto.productId, currentEnterpriseId);
            MpsPeriodMode periodMode = dto.periodMode == null ? MpsPeriodMode.WEEKLY : dto.periodMode;
            int periodCount = normalizePeriodCount(dto.periodCount, periodMode);

            List<MpsRecordEntity> records = rebuildBoard(currentUser.enterprise, product, periodMode, periodCount);
            return ResponseMessage.success(records.stream().map(this::toDto).collect(Collectors.toList()), "Plan maestro generado correctamente", records.size());
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo generar el plan maestro", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo generar el plan maestro", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<MpsRecordDto> update(UUID id, UpdateMpsRecordDto dto) {
        try {
            resolveCurrentAdmin();
            MpsRecordEntity record = mpsRecordRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Registro MPS no encontrado"));

            record.setPlannedProduction(normalize(dto.plannedProduction));
            mpsRecordRepository.save(record);
            recalculateFrom(record);

            return ResponseMessage.success(toDto(record), "Plan maestro actualizado correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo actualizar el plan maestro", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo actualizar el plan maestro", ex.getMessage(), 500);
        }
    }

    private List<MpsRecordEntity> rebuildBoard(EnterpriseEntity enterprise, ProductEntity product, MpsPeriodMode periodMode, int periodCount) {
        mpsRecordRepository.deleteByEnterpriseIdAndProductIdAndPeriodMode(enterprise.getId(), product.getId(), periodMode);

        LocalDate startDate = alignStart(LocalDate.now(), periodMode);
        LocalDate endDate = alignEnd(startDate, periodMode, periodCount);
        Map<LocalDate, DemandTotals> demandTotalsByPeriod = loadDemandTotals(enterprise.getId(), product.getId(), startDate, endDate, periodMode);

        List<MpsRecordEntity> records = new ArrayList<>();
        BigDecimal openingBalance = BigDecimal.ZERO;

        for (int index = 0; index < periodCount; index++) {
            LocalDate periodStart = periodStart(startDate, periodMode, index);
            DemandTotals totals = demandTotalsByPeriod.getOrDefault(periodStart, DemandTotals.empty());
            BigDecimal totalDemand = totals.forecast.add(totals.allocated);
            BigDecimal plannedProduction = suggestedProduction(openingBalance, totalDemand);
            BigDecimal projectedBalance = openingBalance.add(plannedProduction).subtract(totalDemand);

            MpsRecordEntity record = new MpsRecordEntity();
            record.setEnterprise(enterprise);
            record.setProduct(product);
            record.setPeriodMode(periodMode);
            record.setPeriodStartDate(periodStart);
            record.setForecastDemand(normalize(totals.forecast));
            record.setAllocatedDemand(normalize(totals.allocated));
            record.setPlannedProduction(normalize(plannedProduction));
            record.setProjectedAvailableBalance(normalize(projectedBalance));
            records.add(record);

            openingBalance = projectedBalance;
        }

        return mpsRecordRepository.saveAll(records);
    }

    private void recalculateFrom(MpsRecordEntity startRecord) {
        List<MpsRecordEntity> records = mpsRecordRepository.findByEnterpriseIdAndProductIdAndPeriodModeAndPeriodStartDateGreaterThanEqualOrderByPeriodStartDateAsc(
                startRecord.getEnterprise().getId(),
                startRecord.getProduct().getId(),
                startRecord.getPeriodMode(),
                startRecord.getPeriodStartDate()
        );

        BigDecimal openingBalance = previousBalanceBefore(startRecord);

        for (MpsRecordEntity record : records) {
            BigDecimal totalDemand = normalize(record.getForecastDemand()).add(normalize(record.getAllocatedDemand()));
            BigDecimal plannedProduction = normalize(record.getPlannedProduction());
            BigDecimal projectedBalance = openingBalance.add(plannedProduction).subtract(totalDemand);

            record.setProjectedAvailableBalance(normalize(projectedBalance));
            mpsRecordRepository.save(record);
            openingBalance = projectedBalance;
        }
    }

    private BigDecimal previousBalanceBefore(MpsRecordEntity record) {
        return mpsRecordRepository.findByEnterpriseIdAndProductIdAndPeriodModeAndPeriodStartDateLessThanOrderByPeriodStartDateDesc(
                        record.getEnterprise().getId(),
                        record.getProduct().getId(),
                        record.getPeriodMode(),
                        record.getPeriodStartDate()
                )
                .stream()
                .findFirst()
                .map(item -> normalize(item.getProjectedAvailableBalance()))
                .orElse(BigDecimal.ZERO);
    }

    private Map<LocalDate, DemandTotals> loadDemandTotals(UUID enterpriseId, UUID productId, LocalDate startDate, LocalDate endDate, MpsPeriodMode periodMode) {
        List<eccomerce.backend_eccomerce.production.entity.DemandEntity> demands = demandRepository
                .findByEnterpriseIdAndProductIdAndDueDateBetweenAndStatusNotOrderByDueDateAsc(enterpriseId, productId, startDate, endDate, DemandStatus.CANCELLED);

        return demands.stream()
                .collect(Collectors.groupingBy(
                        demand -> periodStart(startDate, periodMode, periodsBetween(startDate, demand.getDueDate(), periodMode)),
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            BigDecimal forecast = sumByType(list, DemandType.FORECAST);
                            BigDecimal allocated = sumByType(list, DemandType.SALE_ORDER);
                            return new DemandTotals(forecast, allocated);
                        })
                ));
    }

    private BigDecimal sumByType(List<eccomerce.backend_eccomerce.production.entity.DemandEntity> demands, DemandType type) {
        return demands.stream()
                .filter(demand -> demand.getType() == type)
                .map(eccomerce.backend_eccomerce.production.entity.DemandEntity::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int periodsBetween(LocalDate startDate, LocalDate dueDate, MpsPeriodMode periodMode) {
        if (periodMode == MpsPeriodMode.MONTHLY) {
            YearMonth startMonth = YearMonth.from(startDate);
            YearMonth dueMonth = YearMonth.from(dueDate);
            return (dueMonth.getYear() - startMonth.getYear()) * 12 + (dueMonth.getMonthValue() - startMonth.getMonthValue());
        }

        return (int) java.time.temporal.ChronoUnit.WEEKS.between(startDate, dueDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
    }

    private LocalDate alignStart(LocalDate date, MpsPeriodMode periodMode) {
        if (periodMode == MpsPeriodMode.MONTHLY) {
            return date.withDayOfMonth(1);
        }

        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private LocalDate alignEnd(LocalDate startDate, MpsPeriodMode periodMode, int periodCount) {
        return periodStart(startDate, periodMode, periodCount - 1).with(periodMode == MpsPeriodMode.MONTHLY
                ? TemporalAdjusters.lastDayOfMonth()
                : TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    private LocalDate periodStart(LocalDate startDate, MpsPeriodMode periodMode, int offset) {
        return periodMode == MpsPeriodMode.MONTHLY ? startDate.plusMonths(offset) : startDate.plusWeeks(offset);
    }

    private MpsRecordDto toDto(MpsRecordEntity record) {
        MpsRecordDto dto = new MpsRecordDto();
        dto.id = record.getId();
        dto.enterpriseId = record.getEnterprise().getId();
        dto.enterpriseName = record.getEnterprise().getName();
        dto.productId = record.getProduct().getId();
        dto.productName = record.getProduct().getName();
        dto.periodMode = record.getPeriodMode();
        dto.periodStartDate = record.getPeriodStartDate();
        dto.forecastDemand = normalize(record.getForecastDemand());
        dto.allocatedDemand = normalize(record.getAllocatedDemand());
        dto.plannedProduction = normalize(record.getPlannedProduction());
        dto.projectedAvailableBalance = normalize(record.getProjectedAvailableBalance());
        return dto;
    }

    private ProductEntity resolveProduct(UUID productId, UUID enterpriseId) {
        return productRepository.findByIdAndEnterpriseId(productId, enterpriseId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    private UserEntity resolveCurrentAdmin() {
        String email = SecurityContextHolder.getContext().getAuthentication() == null
                ? null
                : SecurityContextHolder.getContext().getAuthentication().getName();

        if (email == null) {
            throw new RuntimeException("No tienes permisos para acceder a MPS");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isAdmin = user.role != null && RoleConstants.ADMIN.equalsIgnoreCase(user.role.name);
        if (!isAdmin) {
            throw new RuntimeException("Solo el rol Admin puede gestionar MPS");
        }

        if (user.enterprise == null) {
            throw new RuntimeException("No tienes una empresa asignada");
        }

        return user;
    }

    private void validateRequestedEnterprise(UUID requestedEnterpriseId, UUID currentEnterpriseId) {
        if (requestedEnterpriseId != null && !requestedEnterpriseId.equals(currentEnterpriseId)) {
            throw new RuntimeException("No puedes consultar una empresa distinta a la tuya");
        }
    }

    private int normalizePeriodCount(Integer periodCount, MpsPeriodMode periodMode) {
        if (periodCount != null && periodCount > 0) {
            return periodCount;
        }

        return periodMode == MpsPeriodMode.MONTHLY ? 6 : 12;
    }

    private BigDecimal suggestedProduction(BigDecimal openingBalance, BigDecimal totalDemand) {
        BigDecimal value = totalDemand.subtract(openingBalance);
        return value.compareTo(BigDecimal.ZERO) > 0 ? value : BigDecimal.ZERO;
    }

    private BigDecimal normalize(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }

    private record DemandTotals(BigDecimal forecast, BigDecimal allocated) {
        static DemandTotals empty() {
            return new DemandTotals(BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
}
