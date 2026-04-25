package eccomerce.backend_eccomerce.engineering.service;

import eccomerce.backend_eccomerce.common.constants.RoleConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.engineering.dto.CreateProductDto;
import eccomerce.backend_eccomerce.engineering.dto.ProductVersionDto;
import eccomerce.backend_eccomerce.engineering.dto.UpdateProductDto;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.warehouses.entity.MaterialEntity;
import eccomerce.backend_eccomerce.engineering.entity.ProductEntity;
import eccomerce.backend_eccomerce.engineering.entity.ProductVersionEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.warehouses.repository.MaterialRepository;
import eccomerce.backend_eccomerce.engineering.repository.ProductRepository;
import eccomerce.backend_eccomerce.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ResponseMessage<ProductEntity> create(CreateProductDto dto) {
        try {
            if (isCurrentUserSuperadmin()) {
                return ResponseMessage.error("No se pudo crear el producto", "Superadmin no tiene acceso a productos", 403);
            }

            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            if (productRepository.existsByNameAndEnterpriseId(dto.name, enterprise.getId())) {
                return ResponseMessage.error("No se pudo crear el producto", "Ya existe un producto con ese nombre en tu empresa", 400);
            }

            ProductEntity product = new ProductEntity();
            product.setName(dto.name);
            product.setDescription(dto.description);
            product.setProductionCost(dto.productionCost);
            product.setSalePrice(dto.salePrice);
            product.setEnterprise(enterprise);
            product.setMaterials(resolveMaterials(dto.materialIds, enterprise.getId()));

            for (ProductVersionDto versionDto : dto.versions) {
                ProductVersionEntity version = new ProductVersionEntity();
                version.setName(versionDto.name);
                version.setProduct(product);
                product.getVersions().add(version);
            }

            productRepository.save(product);
            return ResponseMessage.success(product, "Producto creado correctamente", 1);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo crear el producto", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<List<ProductEntity>> findAll() {
        try {
            if (isCurrentUserSuperadmin()) {
                return ResponseMessage.error("No se pudieron obtener los productos", "Superadmin no tiene acceso a productos", 403);
            }

            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            List<ProductEntity> products = productRepository.findByEnterpriseId(enterprise.getId());
            return ResponseMessage.success(products, "Productos obtenidos correctamente", products.size());
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudieron obtener los productos", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<ProductEntity> findById(UUID id) {
        try {
            if (isCurrentUserSuperadmin()) {
                return ResponseMessage.error("No se pudo obtener el producto", "Superadmin no tiene acceso a productos", 403);
            }

            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            ProductEntity product = productRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            return ResponseMessage.success(product, "Producto encontrado", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo obtener el producto", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo obtener el producto", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<ProductEntity> update(UUID id, UpdateProductDto dto) {
        try {
            if (isCurrentUserSuperadmin()) {
                return ResponseMessage.error("No se pudo actualizar el producto", "Superadmin no tiene acceso a productos", 403);
            }

            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            ProductEntity product = productRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (dto.name != null && !dto.name.isBlank()) {
                product.setName(dto.name);
            }
            if (dto.description != null) {
                product.setDescription(dto.description);
            }
            if (dto.productionCost != null) {
                product.setProductionCost(dto.productionCost);
            }
            if (dto.salePrice != null) {
                product.setSalePrice(dto.salePrice);
            }
            if (dto.materialIds != null) {
                product.setMaterials(resolveMaterials(dto.materialIds, enterprise.getId()));
            }
            if (dto.versions != null) {
                if (dto.versions.isEmpty()) {
                    return ResponseMessage.error("No se pudo actualizar el producto", "Debes agregar al menos una versión", 400);
                }
                product.getVersions().clear();
                for (ProductVersionDto versionDto : dto.versions) {
                    ProductVersionEntity version = new ProductVersionEntity();
                    version.setName(versionDto.name);
                    version.setProduct(product);
                    product.getVersions().add(version);
                }
            }

            productRepository.save(product);
            return ResponseMessage.success(product, "Producto actualizado correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo actualizar el producto", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo actualizar el producto", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<Void> delete(UUID id) {
        try {
            if (isCurrentUserSuperadmin()) {
                return ResponseMessage.error("No se pudo eliminar el producto", "Superadmin no tiene acceso a productos", 403);
            }

            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            ProductEntity product = productRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            productRepository.delete(product);
            return ResponseMessage.success(null, "Producto eliminado correctamente", null);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo eliminar el producto", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo eliminar el producto", ex.getMessage(), 500);
        }
    }

    private Set<MaterialEntity> resolveMaterials(List<UUID> materialIds, UUID enterpriseId) {
        Set<MaterialEntity> materials = new HashSet<>();
        for (UUID materialId : materialIds) {
            MaterialEntity material = materialRepository.findByIdAndEnterpriseId(materialId, enterpriseId)
                    .orElseThrow(() -> new RuntimeException("Material no encontrado con id: " + materialId));
            materials.add(material);
        }
        return materials;
    }

    private EnterpriseEntity resolveCurrentEnterprise() {
        UUID enterpriseId = getCurrentEnterpriseId();
        if (enterpriseId == null) {
            throw new RuntimeException("No tienes una empresa asignada");
        }

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
