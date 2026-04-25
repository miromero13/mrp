package eccomerce.backend_eccomerce.workcenter.service;

import eccomerce.backend_eccomerce.workcenter.dto.CreateWorkCenterDto;
import eccomerce.backend_eccomerce.workcenter.dto.UpdateWorkCenterDto;
import eccomerce.backend_eccomerce.workcenter.dto.WorkCenterResponseDto;
import eccomerce.backend_eccomerce.workcenter.entity.WorkCenter;
import eccomerce.backend_eccomerce.workcenter.repository.WorkCenterRepository;
import eccomerce.backend_eccomerce.workcenter.enums.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkCenterService {

    private final WorkCenterRepository workCenterRepository;

    public WorkCenterResponseDto createWorkCenter(CreateWorkCenterDto dto) {
        if (workCenterRepository.findByCode(dto.getCode()).isPresent()) {
            throw new RuntimeException("Centro de trabajo con código " + dto.getCode() + " ya existe");
        }

        WorkCenter workCenter = WorkCenter.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .plant(dto.getPlant())
                .productionLine(dto.getProductionLine())
                .resourceType(dto.getResourceType())
                .capacity(dto.getCapacity())
                .costPerHour(dto.getCostPerHour())
                .targetEfficiency(dto.getTargetEfficiency())
                .isBottleneck(dto.getIsBottleneck())
                .isCriticalResource(dto.getIsCriticalResource())
                .calendar(dto.getCalendar())
                .build();

        WorkCenter saved = workCenterRepository.save(workCenter);
        return mapToDto(saved);
    }

    public WorkCenterResponseDto getWorkCenterById(UUID id) {
        WorkCenter workCenter = workCenterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Centro de trabajo no encontrado"));
        return mapToDto(workCenter);
    }

    public List<WorkCenterResponseDto> getAllWorkCenters() {
        return workCenterRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<WorkCenterResponseDto> getWorkCentersByPlant(String plant) {
        return workCenterRepository.findByPlant(plant).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<WorkCenterResponseDto> getWorkCentersByProductionLine(String productionLine) {
        return workCenterRepository.findByProductionLine(productionLine).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<WorkCenterResponseDto> getActiveWorkCenters() {
        return workCenterRepository.findByStatus(StatusEnum.ACTIVE).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<WorkCenterResponseDto> getCriticalResources() {
        return workCenterRepository.findCriticalResources().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public WorkCenterResponseDto updateWorkCenter(UUID id, UpdateWorkCenterDto dto) {
        WorkCenter workCenter = workCenterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Centro de trabajo no encontrado"));

        if (dto.getName() != null) workCenter.setName(dto.getName());
        if (dto.getDescription() != null) workCenter.setDescription(dto.getDescription());
        if (dto.getProductionLine() != null) workCenter.setProductionLine(dto.getProductionLine());
        if (dto.getResourceType() != null) workCenter.setResourceType(dto.getResourceType());
        if (dto.getCapacity() != null) workCenter.setCapacity(dto.getCapacity());
        if (dto.getCostPerHour() != null) workCenter.setCostPerHour(dto.getCostPerHour());
        if (dto.getTargetEfficiency() != null) workCenter.setTargetEfficiency(dto.getTargetEfficiency());
        if (dto.getCurrentOee() != null) workCenter.setCurrentOee(dto.getCurrentOee());
        if (dto.getIsBottleneck() != null) workCenter.setIsBottleneck(dto.getIsBottleneck());
        if (dto.getIsCriticalResource() != null) workCenter.setIsCriticalResource(dto.getIsCriticalResource());
        if (dto.getStatus() != null) workCenter.setStatus(dto.getStatus());
        if (dto.getCalendar() != null) workCenter.setCalendar(dto.getCalendar());
        if (dto.getNextMaintenanceDate() != null) workCenter.setNextMaintenanceDate(dto.getNextMaintenanceDate());

        WorkCenter updated = workCenterRepository.save(workCenter);
        return mapToDto(updated);
    }

    public void deleteWorkCenter(UUID id) {
        if (!workCenterRepository.existsById(id)) {
            throw new RuntimeException("Centro de trabajo no encontrado");
        }
        workCenterRepository.deleteById(id);
    }

    public void markAsInactive(UUID id) {
        WorkCenter workCenter = workCenterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Centro de trabajo no encontrado"));
        workCenter.setStatus(StatusEnum.INACTIVE);
        workCenterRepository.save(workCenter);
    }

    private WorkCenterResponseDto mapToDto(WorkCenter workCenter) {
        return WorkCenterResponseDto.builder()
                .id(workCenter.getId())
                .code(workCenter.getCode())
                .name(workCenter.getName())
                .description(workCenter.getDescription())
                .plant(workCenter.getPlant())
                .productionLine(workCenter.getProductionLine())
                .resourceType(workCenter.getResourceType())
                .capacity(workCenter.getCapacity())
                .costPerHour(workCenter.getCostPerHour())
                .targetEfficiency(workCenter.getTargetEfficiency())
                .currentOee(workCenter.getCurrentOee())
                .isBottleneck(workCenter.getIsBottleneck())
                .isCriticalResource(workCenter.getIsCriticalResource())
                .status(workCenter.getStatus())
                .calendar(workCenter.getCalendar())
                .lastMaintenanceDate(workCenter.getLastMaintenanceDate())
                .nextMaintenanceDate(workCenter.getNextMaintenanceDate())
                .createdAt(workCenter.getCreatedAt())
                .updatedAt(workCenter.getUpdatedAt())
                .build();
    }
}
