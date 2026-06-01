package eccomerce.backend_eccomerce.reports.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import eccomerce.backend_eccomerce.inventory.entity.InventoryMomentDetailEntity;
import eccomerce.backend_eccomerce.inventory.entity.InventoryMomentEntity;
import eccomerce.backend_eccomerce.inventory.repository.InventoryRepository;
import eccomerce.backend_eccomerce.materials.entity.MaterialEntity;
import eccomerce.backend_eccomerce.materials.repository.MaterialRepository;
import eccomerce.backend_eccomerce.products.entity.ProductEntity;
import eccomerce.backend_eccomerce.products.entity.ProductMaterialEntity;
import eccomerce.backend_eccomerce.products.repository.ProductMaterialRepository;
import eccomerce.backend_eccomerce.products.repository.ProductRepository;
import eccomerce.backend_eccomerce.reports.config.MrpProperties;
import eccomerce.backend_eccomerce.reports.dto.ActionMessageDto;
import eccomerce.backend_eccomerce.reports.dto.ExceptionDto;
import eccomerce.backend_eccomerce.reports.dto.InventorySummaryDto;
import eccomerce.backend_eccomerce.reports.dto.OrdenSugeridaDto;
import eccomerce.backend_eccomerce.reports.dto.gerencial.KpiReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MrpCalculationService {

    private final MaterialRepository materialRepository;
    private final ProductRepository productRepository;
    private final ProductMaterialRepository productMaterialRepository;
    private final InventoryRepository inventoryRepository;
    private final MrpProperties mrpProperties;

    public List<OrdenSugeridaDto> generateOrdenesSugeridas(UUID enterpriseId) {
        List<OrdenSugeridaDto> ordenes = new ArrayList<>();
        LocalDate horizonDate = LocalDate.now().plusDays(mrpProperties.getHorizonDays());

        List<ProductEntity> productsToProduce = inferProductionFromStock(enterpriseId);

        for (ProductEntity product : productsToProduce) {
            BigDecimal productionQty = product.getStockMin().subtract(getProductAvailableStock(product));
            if (productionQty.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            List<ProductMaterialEntity> materials = productMaterialRepository
                    .findMaterialsByProduct(product);

            for (ProductMaterialEntity pm : materials) {
                MaterialEntity material = pm.getMaterial();
                BigDecimal requiredQty = productionQty.multiply(pm.getQuantity());
                BigDecimal availableQty = getMaterialAvailableStock(material, enterpriseId);

                if (availableQty.compareTo(requiredQty) < 0) {
                    BigDecimal orderQty = requiredQty.subtract(availableQty);
                    LocalDate needDate = horizonDate.minusDays(material.getLeadTimeDays() != null ? material.getLeadTimeDays() : 0);

                    ordenes.add(OrdenSugeridaDto.builder()
                            .codigo(material.getCode())
                            .nombre(material.getName())
                            .tipo("MATERIAL")
                            .requerido(requiredQty)
                            .disponible(availableQty)
                            .ordenar(orderQty)
                            .fechaNecesidad(needDate)
                            .accion("COMPRAR")
                            .costoUnitario(material.getUnitCost())
                            .costoTotal(orderQty.multiply(material.getUnitCost()))
                            .build());
                }
            }
        }

        ordenes.sort(Comparator.comparing(OrdenSugeridaDto::getFechaNecesidad));
        return ordenes;
    }

    private List<ProductEntity> inferProductionFromStock(UUID enterpriseId) {
        return productRepository.findProductsWithMinStockAndEnterprise(enterpriseId).stream()
                .filter(p -> getProductAvailableStock(p).compareTo(p.getStockMin()) < 0)
                .collect(Collectors.toList());
    }

    private BigDecimal getProductAvailableStock(ProductEntity product) {
        return productRepository.findById(product.getId())
                .map(p -> p.getStockMin().subtract(p.getStockMin()))
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getMaterialAvailableStock(MaterialEntity material, UUID enterpriseId) {
        BigDecimal onHand = material.getStockCurrent() != null ? material.getStockCurrent() : BigDecimal.ZERO;
        BigDecimal committed = calculateCommittedStock(material);
        BigDecimal onOrder = calculateOnOrderStock(material, enterpriseId);
        return onHand.subtract(committed).add(onOrder);
    }

    private BigDecimal calculateCommittedStock(MaterialEntity material) {
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateOnOrderStock(MaterialEntity material, UUID enterpriseId) {
        LocalDateTime cutoffDate = LocalDateTime.now().plusDays(mrpProperties.getHorizonDays());
        List<InventoryMomentDetailEntity> pendingOrders = inventoryRepository
                .findInventoryHistoryByMaterial(material, cutoffDate);

        return pendingOrders.stream()
                .filter(d -> !d.getIsInput() && "PENDING".equals(d.getInventoryMoment().getStatus()))
                .map(InventoryMomentDetailEntity::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<ActionMessageDto> generateActionMessages(UUID enterpriseId) {
        List<ActionMessageDto> messages = new ArrayList<>();
        LocalDateTime startDate = LocalDateTime.now().minusDays(mrpProperties.getHorizonDays());
        LocalDateTime endDate = LocalDateTime.now();

        List<InventoryMomentEntity> movements = inventoryRepository.findByMovementDateBetweenAndEnterprise(startDate, endDate, enterpriseId);

        for (InventoryMomentEntity movement : movements) {
            if (movement.getType() == InventoryMomentEntity.InventoryMomentType.ORDEN_COMPRA ||
                movement.getType() == InventoryMomentEntity.InventoryMomentType.ORDEN_PRODUCCION) {

                if ("COMPLETED".equals(movement.getStatus())) {
                    LocalDate expectedDate = movement.getCreatedAt().toLocalDate().plusDays(7);
                    LocalDate actualDate = movement.getMovementDate().toLocalDate();

                    if (actualDate.isAfter(expectedDate)) {
                        for (InventoryMomentDetailEntity detail : movement.getInventoryMomentDetails()) {
                            messages.add(ActionMessageDto.builder()
                                    .codigoItem(detail.getMaterial().getCode())
                                    .nombreItem(detail.getMaterial().getName())
                                    .tipoItem("MATERIAL")
                                    .tipoAccion("RETRASAR")
                                    .fechaAnterior(expectedDate)
                                    .fechaNueva(actualDate)
                                    .cantidadAnterior(detail.getQuantity())
                                    .cantidadNueva(detail.getQuantity())
                                    .razon("La orden llegó después de la fecha esperada")
                                    .prioridad("MEDIUM")
                                    .fechaActual(LocalDate.now())
                                    .build());
                        }
                    }
                }
            }
        }

        messages.sort(Comparator.comparing(ActionMessageDto::getPrioridad).reversed());
        return messages;
    }

    public List<ExceptionDto> generateExceptions(UUID enterpriseId) {
        List<ExceptionDto> exceptions = new ArrayList<>();

        List<MaterialEntity> lowStockMaterials = materialRepository.findMaterialsBelowMinStockAndEnterprise(enterpriseId);
        for (MaterialEntity material : lowStockMaterials) {
            exceptions.add(ExceptionDto.builder()
                    .codigo(material.getCode())
                    .nombre(material.getName())
                    .tipo("MATERIAL")
                    .tipoExcepcion("STOCK_BAJOS")
                    .cantidadActual(material.getStockCurrent())
                    .cantidadMinima(material.getStockMin())
                    .cantidadAfectada(material.getStockMin().subtract(material.getStockCurrent()))
                    .severidad("CRITICAL")
                    .estado("PENDING")
                    .descripcion("El stock actual está por debajo del mínimo")
                    .build());
        }

        List<MaterialEntity> excessStockMaterials = materialRepository.findMaterialsWithExcessStockAndEnterprise(enterpriseId);
        for (MaterialEntity material : excessStockMaterials) {
            exceptions.add(ExceptionDto.builder()
                    .codigo(material.getCode())
                    .nombre(material.getName())
                    .tipo("MATERIAL")
                    .tipoExcepcion("EXCESO")
                    .cantidadActual(material.getStockCurrent())
                    .cantidadMinima(material.getStockMin())
                    .cantidadAfectada(material.getStockCurrent().subtract(material.getStockMin()))
                    .severidad("WARNING")
                    .estado("PENDING")
                    .descripcion("El stock actual excede 3 veces el mínimo")
                    .build());
        }

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(mrpProperties.getObsoleteDays());
        List<MaterialEntity> materialsWithStock = materialRepository.findMaterialsWithStockAndEnterprise(enterpriseId);
        for (MaterialEntity material : materialsWithStock) {
            LocalDateTime lastMovement = getLastMovementDate(material);
            if (lastMovement == null || lastMovement.isBefore(cutoffDate)) {
                exceptions.add(ExceptionDto.builder()
                        .codigo(material.getCode())
                        .nombre(material.getName())
                        .tipo("MATERIAL")
                        .tipoExcepcion("OBSOLETO")
                        .cantidadActual(material.getStockCurrent())
                        .cantidadMinima(material.getStockMin())
                        .cantidadAfectada(material.getStockCurrent())
                        .severidad("INFO")
                        .estado("PENDING")
                        .descripcion("Material sin movimientos en los ultimos " + mrpProperties.getObsoleteDays() + " dias")
                        .build());
            }
        }

        exceptions.sort((e1, e2) -> {
            int severityOrder = getSeverityOrder(e1.getSeveridad()) - getSeverityOrder(e2.getSeveridad());
            return severityOrder != 0 ? severityOrder : e1.getCodigo().compareTo(e2.getCodigo());
        });

        return exceptions;
    }

    private int getSeverityOrder(String severity) {
        return switch (severity) {
            case "CRITICAL" -> 0;
            case "WARNING" -> 1;
            case "INFO" -> 2;
            default -> 3;
        };
    }

    public List<InventorySummaryDto> generateInventorySummary(UUID enterpriseId) {
        List<InventorySummaryDto> summaries = new ArrayList<>();
        List<MaterialEntity> materials = materialRepository.findAllActiveAndEnterprise(enterpriseId);

        for (MaterialEntity material : materials) {
            BigDecimal onHand = material.getStockCurrent() != null ? material.getStockCurrent() : BigDecimal.ZERO;
            BigDecimal committed = BigDecimal.ZERO;
            BigDecimal onOrder = calculateOnOrderStock(material, enterpriseId);
            BigDecimal available = onHand.subtract(committed).add(onOrder);

            LocalDateTime lastMovement = getLastMovementDate(material);
            Integer daysWithout = lastMovement != null ?
                    (int) java.time.Duration.between(lastMovement, LocalDateTime.now()).toDays() : null;

            String estado = determineEstado(onHand, material.getStockMin());

            summaries.add(InventorySummaryDto.builder()
                    .codigo(material.getCode())
                    .nombre(material.getName())
                    .unidadMedida(material.getUnitOfMeasure())
                    .stockActual(onHand)
                    .stockMinimo(material.getStockMin())
                    .comprometido(committed)
                    .enOrden(onOrder)
                    .disponibleNeto(available)
                    .costoUnitario(material.getUnitCost())
                    .valorTotal(onHand.multiply(material.getUnitCost()))
                    .ultimoMovimiento(lastMovement != null ? lastMovement.toLocalDate() : null)
                    .diasSinMovimiento(daysWithout)
                    .estado(estado)
                    .build());
        }

        summaries.sort(Comparator.comparing(InventorySummaryDto::getCodigo));
        return summaries;
    }

    private LocalDateTime getLastMovementDate(MaterialEntity material) {
        List<InventoryMomentDetailEntity> history = inventoryRepository
                .findInventoryHistoryByMaterial(material, LocalDateTime.now());
        return history.isEmpty() ? null : history.get(0).getInventoryMoment().getMovementDate();
    }

    private String determineEstado(BigDecimal stock, BigDecimal stockMin) {
        if (stock.compareTo(BigDecimal.ZERO) == 0) {
            return "CRITICO";
        } else if (stock.compareTo(stockMin) < 0) {
            return "BAJO";
        } else if (stock.compareTo(stockMin.multiply(BigDecimal.valueOf(3))) > 0) {
            return "EXCESO";
        }
        return "NORMAL";
    }

    public KpiReportDto getDashboardSummary(UUID enterpriseId) {
        List<OrdenSugeridaDto> ordenes = generateOrdenesSugeridas(enterpriseId);
        List<ActionMessageDto> acciones = generateActionMessages(enterpriseId);
        List<ExceptionDto> excepciones = generateExceptions(enterpriseId);
        List<InventorySummaryDto> inventario = generateInventorySummary(enterpriseId);

        BigDecimal costoTotal = ordenes.stream()
                .map(OrdenSugeridaDto::getCostoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long accionesUrgentes = acciones.stream()
                .filter(a -> "HIGH".equals(a.getPrioridad()))
                .count();

        long excepcionesCriticas = excepciones.stream()
                .filter(e -> "CRITICAL".equals(e.getSeveridad()))
                .count();

        long materialesCriticos = inventario.stream()
                .filter(i -> "CRITICO".equals(i.getEstado()))
                .count();

        long materialesBajos = inventario.stream()
                .filter(i -> "BAJO".equals(i.getEstado()))
                .count();

        BigDecimal valorTotal = inventario.stream()
                .map(InventorySummaryDto::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return KpiReportDto.builder()
                .totalOrdenesSugeridas(ordenes.size())
                .costoTotalOrdenes(costoTotal)
                .totalAcciones(acciones.size())
                .accionesUrgentes(accionesUrgentes)
                .totalExcepciones(excepciones.size())
                .excepcionesCriticas(excepcionesCriticas)
                .materialesCriticos(materialesCriticos)
                .materialesBajos(materialesBajos)
                .valorTotalInventario(valorTotal)
                .build();
    }
}