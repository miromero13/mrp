package eccomerce.backend_eccomerce.warehouses.service;

import eccomerce.backend_eccomerce.common.constants.RoleConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.warehouses.dto.BomItemDto;
import eccomerce.backend_eccomerce.warehouses.dto.CreateBomItemDto;
import eccomerce.backend_eccomerce.warehouses.dto.UpdateBomItemDto;
import eccomerce.backend_eccomerce.engineering.entity.ProductVersionEntity;
import eccomerce.backend_eccomerce.engineering.repository.ProductVersionRepository;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.users.repository.UserRepository;
import eccomerce.backend_eccomerce.warehouses.entity.MaterialEntity;
import eccomerce.backend_eccomerce.warehouses.entity.BomItemEntity;
import eccomerce.backend_eccomerce.warehouses.repository.BomItemRepository;
import eccomerce.backend_eccomerce.warehouses.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BomService {
    @Autowired
    private BomItemRepository bomItemRepository;

    @Autowired
    private ProductVersionRepository productVersionRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseMessage<List<BomItemDto>> findByProductVersion(UUID productVersionId) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            ProductVersionEntity version = productVersionRepository.findByIdAndProductEnterpriseId(productVersionId, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Versión de producto no encontrada"));

            List<BomItemDto> items = bomItemRepository.findByProductVersionId(version.getId()).stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseMessage.success(items, "BOM obtenido correctamente", items.size());
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo obtener el BOM", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo obtener el BOM", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<BomItemDto> create(CreateBomItemDto dto) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            ProductVersionEntity version = resolveWritableVersion(dto.productVersionId, enterprise.getId());
            MaterialEntity material = resolveMaterial(dto.materialId, enterprise.getId());

            if (bomItemRepository.findByProductVersionIdAndMaterialId(version.getId(), material.getId()).isPresent()) {
                return ResponseMessage.error("No se pudo crear el componente", "El material ya existe en esta versión", 400);
            }

            BomItemEntity item = new BomItemEntity();
            item.setProductVersion(version);
            item.setMaterial(material);
            item.setQuantity(dto.quantity);
            item.setScrapFactor(dto.scrapFactor == null ? BigDecimal.ZERO : dto.scrapFactor);

            bomItemRepository.save(item);
            return ResponseMessage.success(toDto(item), "Componente agregado correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo crear el componente", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo crear el componente", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<BomItemDto> update(UUID id, UpdateBomItemDto dto) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            BomItemEntity item = bomItemRepository.findByIdAndProductVersionProductEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Componente BOM no encontrado"));

            ensureVersionIsWritable(item.getProductVersion());

            if (dto.materialId != null) {
                MaterialEntity material = resolveMaterial(dto.materialId, enterprise.getId());
                boolean duplicate = bomItemRepository.findByProductVersionIdAndMaterialId(item.getProductVersion().getId(), material.getId())
                        .filter(existing -> !existing.getId().equals(item.getId()))
                        .isPresent();
                if (duplicate) {
                    return ResponseMessage.error("No se pudo actualizar el componente", "El material ya existe en esta versión", 400);
                }
                item.setMaterial(material);
            }

            if (dto.quantity != null) {
                item.setQuantity(dto.quantity);
            }
            if (dto.scrapFactor != null) {
                item.setScrapFactor(dto.scrapFactor);
            }

            bomItemRepository.save(item);
            return ResponseMessage.success(toDto(item), "Componente actualizado correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo actualizar el componente", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo actualizar el componente", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<Void> delete(UUID id) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            BomItemEntity item = bomItemRepository.findByIdAndProductVersionProductEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Componente BOM no encontrado"));

            ensureVersionIsWritable(item.getProductVersion());

            bomItemRepository.delete(item);
            return ResponseMessage.success(null, "Componente eliminado correctamente", null);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo eliminar el componente", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo eliminar el componente", ex.getMessage(), 500);
        }
    }

    private ProductVersionEntity resolveWritableVersion(UUID versionId, UUID enterpriseId) {
        ProductVersionEntity version = productVersionRepository.findByIdAndProductEnterpriseId(versionId, enterpriseId)
                .orElseThrow(() -> new RuntimeException("Versión de producto no encontrada"));
        ensureVersionIsWritable(version);
        return version;
    }

    private void ensureVersionIsWritable(ProductVersionEntity version) {
        if (version.isApproved() || version.isUsedInProduction()) {
            throw new RuntimeException("No se puede modificar un BOM aprobado o ya usado en producción");
        }
    }

    private MaterialEntity resolveMaterial(UUID materialId, UUID enterpriseId) {
        return materialRepository.findByIdAndEnterpriseId(materialId, enterpriseId)
                .orElseThrow(() -> new RuntimeException("Material no encontrado"));
    }

    private BomItemDto toDto(BomItemEntity item) {
        BomItemDto dto = new BomItemDto();
        dto.id = item.getId();
        dto.productVersionId = item.getProductVersion().getId();
        dto.productVersionName = item.getProductVersion().getName();
        dto.approved = item.getProductVersion().isApproved();
        dto.usedInProduction = item.getProductVersion().isUsedInProduction();
        dto.materialId = item.getMaterial().getId();
        dto.materialCode = item.getMaterial().getCode();
        dto.materialUnitOfMeasure = item.getMaterial().getUnitOfMeasure();
        dto.materialUnitPrice = item.getMaterial().getUnitPrice();
        dto.quantity = item.getQuantity();
        dto.scrapFactor = item.getScrapFactor();
        dto.estimatedCost = calculateEstimatedCost(item.getQuantity(), item.getScrapFactor(), item.getMaterial().getUnitPrice());
        return dto;
    }

    private BigDecimal calculateEstimatedCost(BigDecimal quantity, BigDecimal scrapFactor, BigDecimal unitPrice) {
        BigDecimal scrapPercentage = scrapFactor == null ? BigDecimal.ZERO : scrapFactor;
        BigDecimal scrapMultiplier = BigDecimal.ONE.add(scrapPercentage.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP));
        return unitPrice.multiply(quantity).multiply(scrapMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private EnterpriseEntity resolveCurrentEnterprise() {
        UUID enterpriseId = getCurrentEnterpriseId();
        if (enterpriseId == null) {
            throw new RuntimeException("No tienes una empresa asignada");
        }

        return enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
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
