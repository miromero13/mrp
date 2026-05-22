package eccomerce.backend_eccomerce.warehouses.service;

import eccomerce.backend_eccomerce.common.constants.RoleConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.engineering.entity.ProductEntity;
import eccomerce.backend_eccomerce.engineering.repository.ProductRepository;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.users.entity.UserEntity;
import eccomerce.backend_eccomerce.users.repository.UserRepository;
import eccomerce.backend_eccomerce.warehouses.dto.BatchDto;
import eccomerce.backend_eccomerce.warehouses.dto.CreateBatchDto;
import eccomerce.backend_eccomerce.warehouses.dto.UpdateBatchDto;
import eccomerce.backend_eccomerce.warehouses.entity.BatchEntity;
import eccomerce.backend_eccomerce.warehouses.entity.MaterialEntity;
import eccomerce.backend_eccomerce.warehouses.entity.MaterialMovementDetailEntity;
import eccomerce.backend_eccomerce.warehouses.entity.MaterialMovementEntity;
import eccomerce.backend_eccomerce.warehouses.enums.BatchStatus;
import eccomerce.backend_eccomerce.warehouses.enums.MaterialMovementType;
import eccomerce.backend_eccomerce.warehouses.repository.BatchRepository;
import eccomerce.backend_eccomerce.warehouses.repository.MaterialMovementRepository;
import eccomerce.backend_eccomerce.warehouses.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BatchService {
    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MaterialMovementRepository materialMovementRepository;

    public ResponseMessage<List<BatchDto>> findAll(UUID enterpriseId, UUID materialId, UUID productId) {
        try {
            UserEntity currentUser = resolveCurrentUser();
            UUID effectiveEnterpriseId = resolveEnterpriseFilter(currentUser, enterpriseId);

            List<BatchEntity> batches;
            if (materialId != null) {
                MaterialEntity material = resolveMaterial(materialId, effectiveEnterpriseId);
                batches = batchRepository.findByMaterialIdOrderByProductionDateDesc(material.getId());
            } else if (productId != null) {
                ProductEntity product = resolveProduct(productId, effectiveEnterpriseId);
                batches = batchRepository.findByProductIdOrderByProductionDateDesc(product.getId());
            } else {
                batches = batchRepository.findAllForEnterprise(effectiveEnterpriseId);
            }

            syncExpiredStatuses(batches);
            return ResponseMessage.success(batches.stream().map(this::toDto).collect(Collectors.toList()), "Lotes obtenidos correctamente", batches.size());
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudieron obtener los lotes", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudieron obtener los lotes", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<BatchDto> findById(UUID id) {
        try {
            UserEntity currentUser = resolveCurrentUser();
            BatchEntity batch = resolveBatchForCurrentUser(id, currentUser);
            syncExpiredStatus(batch);
            return ResponseMessage.success(toDto(batch), "Lote encontrado", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo obtener el lote", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo obtener el lote", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<BatchDto> create(CreateBatchDto dto) {
        try {
            UserEntity currentUser = resolveCurrentUser();
            if (batchRepository.existsByBatchNumber(dto.batchNumber.trim())) {
                return ResponseMessage.error("No se pudo crear el lote", "Ya existe un lote con ese número", 400);
            }

            BatchEntity batch = new BatchEntity();
            batch.setBatchNumber(dto.batchNumber.trim());
            batch.setProductionDate(dto.productionDate);
            batch.setExpirationDate(dto.expirationDate);
            batch.setCurrentStock(dto.currentStock);
            batch.setStatus(normalizeStatus(dto.status, dto.expirationDate));

            if (dto.materialId != null && dto.productId != null) {
                return ResponseMessage.error("No se pudo crear el lote", "Solo debes seleccionar un material o un producto", 400);
            }

            if (dto.materialId == null && dto.productId == null) {
                return ResponseMessage.error("No se pudo crear el lote", "Debes seleccionar un material o un producto", 400);
            }

            if (dto.materialId != null) {
                MaterialEntity material = resolveMaterial(dto.materialId, resolveEnterpriseFilter(currentUser, null));
                batch.setMaterial(material);
                material.setStockCurrent(material.getStockCurrent().add(dto.currentStock));
            }

            if (dto.productId != null) {
                ProductEntity product = resolveProduct(dto.productId, resolveEnterpriseFilter(currentUser, null));
                batch.setProduct(product);
            }

            batchRepository.save(batch);
            if (batch.getMaterial() != null) {
                registerAutomaticEntryMovement(batch, batch.getMaterial(), dto.currentStock);
            }
            return ResponseMessage.success(toDto(batch), "Lote creado correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo crear el lote", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo crear el lote", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<BatchDto> update(UUID id, UpdateBatchDto dto) {
        try {
            UserEntity currentUser = resolveCurrentUser();
            BatchEntity batch = resolveBatchForCurrentUser(id, currentUser);

            if (dto.expirationDate != null) {
                batch.setExpirationDate(dto.expirationDate);
            }
            if (dto.status != null) {
                batch.setStatus(normalizeStatus(dto.status, batch.getExpirationDate()));
            }

            syncExpiredStatus(batch);
            batchRepository.save(batch);
            return ResponseMessage.success(toDto(batch), "Lote actualizado correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo actualizar el lote", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo actualizar el lote", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<Void> delete(UUID id) {
        try {
            UserEntity currentUser = resolveCurrentUser();
            BatchEntity batch = resolveBatchForCurrentUser(id, currentUser);

            if (batch.getCurrentStock().compareTo(BigDecimal.ZERO) > 0) {
                return ResponseMessage.error("No se pudo eliminar el lote", "El lote aún tiene stock disponible", 400);
            }

            batchRepository.delete(batch);
            return ResponseMessage.success(null, "Lote eliminado correctamente", null);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo eliminar el lote", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo eliminar el lote", ex.getMessage(), 500);
        }
    }

    public BatchEntity resolveBatchForMovement(UUID batchId, UUID materialId, BigDecimal quantity, boolean exitMovement) {
        UserEntity currentUser = resolveCurrentUser();
        MaterialEntity material = resolveMaterial(materialId, resolveEnterpriseFilter(currentUser, null));

        BatchEntity batch;
        if (batchId != null) {
            batch = resolveBatchForCurrentUser(batchId, currentUser);
        } else if (exitMovement) {
            batch = resolveBatchForExitMovement(material.getId(), quantity, currentUser);
        } else {
            throw new RuntimeException("Debes seleccionar un lote");
        }

        if (batch.getMaterial() == null || !batch.getMaterial().getId().equals(material.getId())) {
            throw new RuntimeException("El lote no pertenece al material seleccionado");
        }

        syncExpiredStatus(batch);
        if (batch.getExpirationDate() != null && !batch.getExpirationDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("El lote está vencido y no puede usarse");
        }

        if (exitMovement && batch.getStatus() != BatchStatus.APPROVED) {
            throw new RuntimeException("El lote debe estar aprobado para despachos");
        }

        if (exitMovement && batch.getCurrentStock().compareTo(quantity) < 0) {
            throw new RuntimeException("El lote no tiene stock suficiente");
        }

        return batch;
    }

    private BatchEntity resolveBatchForExitMovement(UUID materialId, BigDecimal quantity, UserEntity currentUser) {
        List<BatchEntity> batches = batchRepository.findByMaterialIdOrderByProductionDateAsc(materialId);
        for (BatchEntity batch : batches) {
            syncExpiredStatus(batch);
            if (batch.getStatus() == BatchStatus.APPROVED && batch.getCurrentStock().compareTo(quantity) >= 0) {
                return batch;
            }
        }

        throw new RuntimeException("No hay lotes disponibles para la salida");
    }

    @Transactional
    public void applyMovementToBatch(BatchEntity batch, BigDecimal quantity, boolean entry) {
        batch.setCurrentStock(entry ? batch.getCurrentStock().add(quantity) : batch.getCurrentStock().subtract(quantity));
        batchRepository.save(batch);
    }

    private void registerAutomaticEntryMovement(BatchEntity batch, MaterialEntity material, BigDecimal quantity) {
        MaterialMovementEntity movement = new MaterialMovementEntity();
        movement.setMaterial(material);
        movement.setType(MaterialMovementType.ENTRY);
        movement.setReason("Entrada automática por creación de lote");

        MaterialMovementDetailEntity detail = new MaterialMovementDetailEntity();
        detail.setQuantity(quantity);
        detail.setUnitPrice(material.getUnitPrice());
        detail.setMaterialMovement(movement);
        detail.setBatch(batch);
        movement.getDetails().add(detail);

        materialMovementRepository.save(movement);
    }

    private void syncExpiredStatuses(List<BatchEntity> batches) {
        for (BatchEntity batch : batches) {
            syncExpiredStatus(batch);
        }
    }

    private void syncExpiredStatus(BatchEntity batch) {
        if (batch.getExpirationDate() != null && !batch.getExpirationDate().isAfter(LocalDate.now()) && batch.getStatus() != BatchStatus.BLOCKED) {
            batch.setStatus(BatchStatus.BLOCKED);
            batchRepository.save(batch);
        }
    }

    private BatchStatus normalizeStatus(BatchStatus requestedStatus, LocalDate expirationDate) {
        if (expirationDate != null && !expirationDate.isAfter(LocalDate.now())) {
            return BatchStatus.BLOCKED;
        }
        return requestedStatus == null ? BatchStatus.APPROVED : requestedStatus;
    }

    private BatchDto toDto(BatchEntity batch) {
        BatchDto dto = new BatchDto();
        dto.id = batch.getId();
        dto.batchNumber = batch.getBatchNumber();
        dto.materialId = batch.getMaterial() == null ? null : batch.getMaterial().getId();
        dto.materialCode = batch.getMaterial() == null ? null : batch.getMaterial().getCode();
        dto.productId = batch.getProduct() == null ? null : batch.getProduct().getId();
        dto.productName = batch.getProduct() == null ? null : batch.getProduct().getName();
        dto.productionDate = batch.getProductionDate();
        dto.expirationDate = batch.getExpirationDate();
        dto.currentStock = batch.getCurrentStock();
        dto.status = batch.getStatus();
        dto.expired = batch.getExpirationDate() != null && !batch.getExpirationDate().isAfter(LocalDate.now());
        return dto;
    }

    private UserEntity resolveCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication() == null
                ? null
                : SecurityContextHolder.getContext().getAuthentication().getName();

        if (email == null) {
            throw new RuntimeException("No tienes una sesión activa");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    private UUID resolveEnterpriseFilter(UserEntity currentUser, UUID requestedEnterpriseId) {
        boolean isSuperadmin = currentUser.role != null && RoleConstants.SUPERADMIN.equalsIgnoreCase(currentUser.role.name);
        if (!isSuperadmin) {
            if (currentUser.enterprise == null) {
                throw new RuntimeException("No tienes una empresa asignada");
            }
            if (requestedEnterpriseId != null && !requestedEnterpriseId.equals(currentUser.enterprise.getId())) {
                throw new RuntimeException("No puedes consultar otra empresa");
            }
            return currentUser.enterprise.getId();
        }

        return requestedEnterpriseId;
    }

    private MaterialEntity resolveMaterial(UUID materialId, UUID enterpriseId) {
        MaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material no encontrado"));

        if (enterpriseId != null && (material.getEnterprise() == null || !enterpriseId.equals(material.getEnterprise().getId()))) {
            throw new RuntimeException("El material no pertenece a tu empresa");
        }

        return material;
    }

    private ProductEntity resolveProduct(UUID productId, UUID enterpriseId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (enterpriseId != null && (product.getEnterprise() == null || !enterpriseId.equals(product.getEnterprise().getId()))) {
            throw new RuntimeException("El producto no pertenece a tu empresa");
        }

        return product;
    }

    private BatchEntity resolveBatchForCurrentUser(UUID id, UserEntity currentUser) {
        BatchEntity batch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        if (currentUser.role == null || !RoleConstants.SUPERADMIN.equalsIgnoreCase(currentUser.role.name)) {
            UUID enterpriseId = currentUser.enterprise == null ? null : currentUser.enterprise.getId();
            boolean matchesMaterial = batch.getMaterial() != null && batch.getMaterial().getEnterprise() != null && enterpriseId != null && enterpriseId.equals(batch.getMaterial().getEnterprise().getId());
            boolean matchesProduct = batch.getProduct() != null && batch.getProduct().getEnterprise() != null && enterpriseId != null && enterpriseId.equals(batch.getProduct().getEnterprise().getId());

            if (!matchesMaterial && !matchesProduct) {
                throw new RuntimeException("El lote no pertenece a tu empresa");
            }
        }

        return batch;
    }
}
