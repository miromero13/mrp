package eccomerce.backend_eccomerce.indirectcost.service;

import eccomerce.backend_eccomerce.common.exception.BusinessException;
import eccomerce.backend_eccomerce.indirectcost.dto.IndirectCostRequestDto;
import eccomerce.backend_eccomerce.indirectcost.dto.IndirectCostResponseDto;
import eccomerce.backend_eccomerce.indirectcost.entity.*;
import eccomerce.backend_eccomerce.indirectcost.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndirectCostService {

    private final IndirectCostRepository indirectCostRepository;
    private final IndirectCostCategoryRepository categoryRepository;
    private final CostCenterRepository costCenterRepository;
    private final IndirectCostHistoryRepository historyRepository;

    @Transactional
    public IndirectCostResponseDto create(IndirectCostRequestDto dto) {
        IndirectCostCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BusinessException("Categoría no encontrada"));
        
        CostCenter costCenter = costCenterRepository.findById(dto.getCostCenterId())
                .orElseThrow(() -> new BusinessException("Centro de costo no encontrado"));

        validateNoOverlap(category, costCenter, dto.getStartDate(), dto.getEndDate(), null);

        IndirectCost cost = new IndirectCost();
        mapDtoToEntity(dto, cost, category, costCenter);
        
        cost = indirectCostRepository.save(cost);
        return mapToResponseDto(cost);
    }

    @Transactional
    public IndirectCostResponseDto update(UUID id, IndirectCostRequestDto dto) {
        IndirectCost existing = indirectCostRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Costo indirecto no encontrado"));

        IndirectCostCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BusinessException("Categoría no encontrada"));
        
        CostCenter costCenter = costCenterRepository.findById(dto.getCostCenterId())
                .orElseThrow(() -> new BusinessException("Centro de costo no encontrado"));

        validateNoOverlap(category, costCenter, dto.getStartDate(), dto.getEndDate(), id);

        // Record history before update
        recordHistory(existing, dto);

        mapDtoToEntity(dto, existing, category, costCenter);
        
        existing = indirectCostRepository.save(existing);
        return mapToResponseDto(existing);
    }

    @Transactional
    public void deactivate(UUID id) {
        IndirectCost cost = indirectCostRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Costo indirecto no encontrado"));
        cost.setActive(false);
        indirectCostRepository.save(cost);
    }

    public List<IndirectCostResponseDto> findAll() {
        return indirectCostRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private void validateNoOverlap(IndirectCostCategory category, CostCenter costCenter, 
                                 java.time.LocalDate start, java.time.LocalDate end, UUID id) {
        if (start.isAfter(end)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        boolean overlaps = indirectCostRepository.existsOverlapping(category, costCenter, start, end, id);
        if (overlaps) {
            throw new BusinessException("Ya existe un costo de este tipo para el centro de costo en el periodo especificado");
        }
    }

    private void recordHistory(IndirectCost old, IndirectCostRequestDto dto) {
        IndirectCostHistory history = new IndirectCostHistory();
        history.setIndirectCostId(old.getId());
        
        history.setAmountPrevious(old.getAmount());
        history.setAmountNew(dto.getAmount());
        
        history.setStartDatePrevious(old.getStartDate());
        history.setStartDateNew(dto.getStartDate());
        
        history.setEndDatePrevious(old.getEndDate());
        history.setEndDateNew(dto.getEndDate());
        
        history.setDistributionCriterionPrevious(old.getDistributionCriterion());
        history.setDistributionCriterionNew(dto.getDistributionCriterion());
        
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        history.setModifiedBy(currentUser);
        
        historyRepository.save(history);
    }

    private void mapDtoToEntity(IndirectCostRequestDto dto, IndirectCost entity, 
                               IndirectCostCategory category, CostCenter costCenter) {
        entity.setCategory(category);
        entity.setCostCenter(costCenter);
        entity.setAmount(dto.getAmount());
        entity.setCurrency(dto.getCurrency());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setDistributionCriterion(dto.getDistributionCriterion());
    }

    private IndirectCostResponseDto mapToResponseDto(IndirectCost entity) {
        IndirectCostResponseDto dto = new IndirectCostResponseDto();
        dto.setId(entity.getId());
        dto.setCategoryId(entity.getCategory().getId());
        dto.setCategoryName(entity.getCategory().getName());
        dto.setAmount(entity.getAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setDistributionCriterion(entity.getDistributionCriterion());
        dto.setCostCenterId(entity.getCostCenter().getId());
        dto.setCostCenterName(entity.getCostCenter().getName());
        dto.setActive(entity.getActive());
        return dto;
    }
}
