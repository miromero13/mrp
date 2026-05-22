package eccomerce.backend_eccomerce.warehouses.service;

import eccomerce.backend_eccomerce.common.constants.RoleConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.warehouses.entity.BatchEntity;
import eccomerce.backend_eccomerce.warehouses.dto.CreateMaterialDto;
import eccomerce.backend_eccomerce.warehouses.dto.CreateMaterialMovementDto;
import eccomerce.backend_eccomerce.warehouses.dto.UpdateMaterialDto;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.warehouses.entity.MaterialEntity;
import eccomerce.backend_eccomerce.warehouses.entity.MaterialMovementEntity;
import eccomerce.backend_eccomerce.warehouses.entity.MaterialMovementDetailEntity;
import eccomerce.backend_eccomerce.warehouses.enums.MaterialMovementType;
import eccomerce.backend_eccomerce.warehouses.service.BatchService;
import eccomerce.backend_eccomerce.warehouses.repository.MaterialRepository;
import eccomerce.backend_eccomerce.warehouses.repository.MaterialMovementRepository;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MaterialService {
    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialMovementRepository materialMovementRepository;

    @Autowired
    private BatchService batchService;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ResponseMessage<MaterialEntity> create(CreateMaterialDto dto) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            if (materialRepository.existsByCodeAndEnterpriseId(dto.code, enterprise.getId())) {
                return ResponseMessage.error("No se pudo crear el material", "Ya existe un material con ese código en tu empresa", 400);
            }

            MaterialEntity material = new MaterialEntity();
            material.setCode(dto.code);
            material.setUnitOfMeasure(dto.unitOfMeasure);
            material.setStockMin(dto.stockMin);
            material.setStockCurrent(dto.stockCurrent);
            material.setUnitPrice(dto.unitPrice);
            material.setEnterprise(enterprise);

            materialRepository.save(material);
            return ResponseMessage.success(material, "Material creado correctamente", 1);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo crear el material", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<List<MaterialEntity>> findAll(UUID enterpriseId) {
        try {
            EnterpriseEntity enterprise = enterpriseId != null ? resolveEnterprise(enterpriseId) : resolveCurrentEnterprise();
            List<MaterialEntity> materials = materialRepository.findByEnterpriseId(enterprise.getId());
            return ResponseMessage.success(materials, "Materiales obtenidos correctamente", materials.size());
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudieron obtener los materiales", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<MaterialEntity> findById(UUID id) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            MaterialEntity material = materialRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Material no encontrado"));
            return ResponseMessage.success(material, "Material encontrado", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo obtener el material", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo obtener el material", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<List<MaterialMovementEntity>> findMovements() {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            List<MaterialMovementEntity> movements = materialMovementRepository.findByEnterpriseIdOrderByCreatedAtDesc(enterprise.getId());
            return ResponseMessage.success(movements, "Movimientos obtenidos correctamente", movements.size());
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudieron obtener los movimientos", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<List<MaterialMovementEntity>> findMovementsByMaterial(UUID materialId) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            MaterialEntity material = materialRepository.findByIdAndEnterpriseId(materialId, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Material no encontrado"));
            List<MaterialMovementEntity> movements = materialMovementRepository.findByMaterialIdOrderByCreatedAtDesc(material.getId());
            return ResponseMessage.success(movements, "Movimientos obtenidos correctamente", movements.size());
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudieron obtener los movimientos", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudieron obtener los movimientos", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<MaterialMovementEntity> registerMovement(UUID materialId, CreateMaterialMovementDto dto) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            MaterialEntity material = materialRepository.findByIdAndEnterpriseId(materialId, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Material no encontrado"));

            if (dto.type == MaterialMovementType.EXIT && material.getStockCurrent().compareTo(dto.quantity) < 0) {
                return ResponseMessage.error("No se pudo registrar el movimiento", "No hay stock suficiente para la salida", 400);
            }

            BatchEntity batch = batchService.resolveBatchForMovement(dto.batchId, material.getId(), dto.quantity, dto.type == MaterialMovementType.EXIT);

            MaterialMovementEntity movement = new MaterialMovementEntity();
            movement.setMaterial(material);
            movement.setType(dto.type);
            movement.setReason(dto.reason == null ? "" : dto.reason.trim());

            MaterialMovementDetailEntity detail = new MaterialMovementDetailEntity();
            detail.setQuantity(dto.quantity);
            detail.setUnitPrice(dto.unitPrice);
            detail.setMaterialMovement(movement);
            detail.setBatch(batch);
            movement.getDetails().add(detail);

            if (dto.type == MaterialMovementType.ENTRY) {
                material.setStockCurrent(material.getStockCurrent().add(dto.quantity));
                batchService.applyMovementToBatch(batch, dto.quantity, true);
            } else {
                material.setStockCurrent(material.getStockCurrent().subtract(dto.quantity));
                batchService.applyMovementToBatch(batch, dto.quantity, false);
            }

            materialRepository.save(material);
            materialMovementRepository.save(movement);

            return ResponseMessage.success(movement, "Movimiento registrado correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo registrar el movimiento", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo registrar el movimiento", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<MaterialEntity> update(UUID id, UpdateMaterialDto dto) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            MaterialEntity material = materialRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Material no encontrado"));

            if (dto.code != null && !dto.code.isBlank()) {
                material.setCode(dto.code);
            }
            if (dto.unitOfMeasure != null && !dto.unitOfMeasure.isBlank()) {
                material.setUnitOfMeasure(dto.unitOfMeasure);
            }
            if (dto.stockMin != null) {
                material.setStockMin(dto.stockMin);
            }
            if (dto.stockCurrent != null) {
                material.setStockCurrent(dto.stockCurrent);
            }
            if (dto.unitPrice != null) {
                material.setUnitPrice(dto.unitPrice);
            }

            materialRepository.save(material);
            return ResponseMessage.success(material, "Material actualizado correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo actualizar el material", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo actualizar el material", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<Void> delete(UUID id) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            MaterialEntity material = materialRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Material no encontrado"));

            materialRepository.delete(material);
            return ResponseMessage.success(null, "Material eliminado correctamente", null);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo eliminar el material", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo eliminar el material", ex.getMessage(), 500);
        }
    }

    private EnterpriseEntity resolveCurrentEnterprise() {
        UUID enterpriseId = getCurrentEnterpriseId();
        if (enterpriseId == null) {
            throw new RuntimeException("No tienes una empresa asignada");
        }

        return enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
    }

    private EnterpriseEntity resolveEnterprise(UUID enterpriseId) {
        return enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
    }

    private boolean isCurrentUserSuperadmin() {
        String email = SecurityContextHolder.getContext().getAuthentication() == null
                ? null
                : SecurityContextHolder.getContext().getAuthentication().getName();

        if (email == null) {
            return false;
        }

        return userRepository.findByEmail(email)
                .map(user -> user.role != null && RoleConstants.SUPERADMIN.equalsIgnoreCase(user.role.name))
                .orElse(false);
    }

    private UUID getCurrentEnterpriseId() {
        String email = SecurityContextHolder.getContext().getAuthentication() == null
                ? null
                : SecurityContextHolder.getContext().getAuthentication().getName();

        if (email == null) {
            return null;
        }

        return userRepository.findByEmail(email)
                .map(user -> user.enterprise == null ? null : user.enterprise.getId())
                .orElse(null);
    }
}
