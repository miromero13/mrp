package eccomerce.backend_eccomerce.engineering.service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.engineering.dto.CreateProductionRouteDto;
import eccomerce.backend_eccomerce.engineering.dto.CreateRouteOperationDto;
import eccomerce.backend_eccomerce.engineering.dto.ProductionRouteDto;
import eccomerce.backend_eccomerce.engineering.dto.RouteOperationDto;
import eccomerce.backend_eccomerce.engineering.dto.UpdateProductionRouteDto;
import eccomerce.backend_eccomerce.engineering.entity.MachineEntity;
import eccomerce.backend_eccomerce.engineering.entity.ProductVersionEntity;
import eccomerce.backend_eccomerce.engineering.entity.ProductionRouteEntity;
import eccomerce.backend_eccomerce.engineering.entity.RouteOperationEntity;
import eccomerce.backend_eccomerce.engineering.repository.ProductionRouteRepository;
import eccomerce.backend_eccomerce.engineering.repository.ProductVersionRepository;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.users.repository.UserRepository;
import eccomerce.backend_eccomerce.engineering.repository.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductionRouteService {
    @Autowired
    private ProductionRouteRepository productionRouteRepository;

    @Autowired
    private ProductVersionRepository productVersionRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseMessage<List<ProductionRouteDto>> findAll(UUID productVersionId) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            List<ProductionRouteEntity> routes;
            if (productVersionId == null) {
                routes = productionRouteRepository.findByProductVersionProductEnterpriseIdOrderByCreatedAtDesc(enterprise.getId());
            } else {
                resolveProductVersion(productVersionId, enterprise.getId());
                routes = productionRouteRepository.findByProductVersionIdOrderByCreatedAtDesc(productVersionId);
            }

            List<ProductionRouteDto> items = routes.stream().map(this::toDto).collect(Collectors.toList());
            return ResponseMessage.success(items, "Rutas obtenidas correctamente", items.size());
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudieron obtener las rutas", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudieron obtener las rutas", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<ProductionRouteDto> findById(UUID id) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            ProductionRouteEntity route = productionRouteRepository.findByIdAndProductVersionProductEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
            return ResponseMessage.success(toDto(route), "Ruta encontrada", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo obtener la ruta", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo obtener la ruta", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<ProductionRouteDto> create(CreateProductionRouteDto dto) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            ProductVersionEntity version = resolveProductVersion(dto.productVersionId, enterprise.getId());
            validateOperations(dto.operations);

            ProductionRouteEntity route = new ProductionRouteEntity();
            route.setName(dto.name.trim());
            route.setActive(dto.isActive == null || dto.isActive);
            route.setProductVersion(version);
            route.getOperations().addAll(buildOperations(dto.operations, route, enterprise.getId()));

            if (route.isActive()) {
                deactivateOtherActiveRoutes(version.getId(), null);
            }

            productionRouteRepository.save(route);
            return ResponseMessage.success(toDto(route), "Ruta creada correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo crear la ruta", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo crear la ruta", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<ProductionRouteDto> update(UUID id, UpdateProductionRouteDto dto) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            ProductionRouteEntity route = productionRouteRepository.findByIdAndProductVersionProductEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

            if (dto.name != null && !dto.name.isBlank()) {
                route.setName(dto.name.trim());
            }
            if (dto.isActive != null) {
                route.setActive(dto.isActive);
                if (dto.isActive) {
                    deactivateOtherActiveRoutes(route.getProductVersion().getId(), route.getId());
                }
            }
            if (dto.operations != null) {
                validateOperations(dto.operations);
                route.getOperations().clear();
                route.getOperations().addAll(buildOperations(dto.operations, route, enterprise.getId()));
            }

            productionRouteRepository.save(route);
            return ResponseMessage.success(toDto(route), "Ruta actualizada correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo actualizar la ruta", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo actualizar la ruta", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<Void> delete(UUID id) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            ProductionRouteEntity route = productionRouteRepository.findByIdAndProductVersionProductEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

            productionRouteRepository.delete(route);
            return ResponseMessage.success(null, "Ruta eliminada correctamente", null);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo eliminar la ruta", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo eliminar la ruta", ex.getMessage(), 500);
        }
    }

    private void validateOperations(List<CreateRouteOperationDto> operations) {
        if (operations == null || operations.isEmpty()) {
            throw new RuntimeException("Debes agregar al menos una operación");
        }

        Set<Integer> orders = new HashSet<>();
        for (CreateRouteOperationDto operation : operations) {
            if (operation.operationOrder == null || operation.operationOrder < 1) {
                throw new RuntimeException("El orden de cada paso debe ser mayor a cero");
            }
            if (!orders.add(operation.operationOrder)) {
                throw new RuntimeException("No puedes repetir el número de paso");
            }
        }
    }

    private List<RouteOperationEntity> buildOperations(List<CreateRouteOperationDto> dtos, ProductionRouteEntity route, UUID enterpriseId) {
        return dtos.stream().map(dto -> {
            RouteOperationEntity operation = new RouteOperationEntity();
            operation.setProductionRoute(route);
            operation.setOperationOrder(dto.operationOrder);
            operation.setDescription(dto.description.trim());
            operation.setSetupTimeMinutes(dto.setupTimeMinutes);
            operation.setRunTimeMinutes(dto.runTimeMinutes);
            if (dto.machineId != null) {
                operation.setMachine(resolveMachine(dto.machineId, enterpriseId));
            }
            return operation;
        }).collect(Collectors.toList());
    }

    private MachineEntity resolveMachine(UUID machineId, UUID enterpriseId) {
        return machineRepository.findByIdAndEnterpriseId(machineId, enterpriseId)
                .orElseThrow(() -> new RuntimeException("Maquinaria no encontrada"));
    }

    private ProductVersionEntity resolveProductVersion(UUID versionId, UUID enterpriseId) {
        return productVersionRepository.findByIdAndProductEnterpriseId(versionId, enterpriseId)
                .orElseThrow(() -> new RuntimeException("Versión de producto no encontrada"));
    }

    private void deactivateOtherActiveRoutes(UUID versionId, UUID currentRouteId) {
        List<ProductionRouteEntity> activeRoutes = productionRouteRepository.findByProductVersionIdAndIsActiveTrue(versionId);
        for (ProductionRouteEntity activeRoute : activeRoutes) {
            if (currentRouteId == null || !activeRoute.getId().equals(currentRouteId)) {
                activeRoute.setActive(false);
                productionRouteRepository.save(activeRoute);
            }
        }
    }

    private ProductionRouteDto toDto(ProductionRouteEntity route) {
        ProductionRouteDto dto = new ProductionRouteDto();
        dto.id = route.getId();
        dto.productVersionId = route.getProductVersion().getId();
        dto.productVersionName = route.getProductVersion().getName();
        dto.productId = route.getProductVersion().getProduct().getId();
        dto.productName = route.getProductVersion().getProduct().getName();
        dto.name = route.getName();
        dto.isActive = route.isActive();
        dto.operations = route.getOperations().stream().map(this::toOperationDto).collect(Collectors.toList());
        return dto;
    }

    private RouteOperationDto toOperationDto(RouteOperationEntity operation) {
        RouteOperationDto dto = new RouteOperationDto();
        dto.id = operation.getId();
        dto.machineId = operation.getMachine() == null ? null : operation.getMachine().getId();
        dto.machineName = operation.getMachine() == null ? null : operation.getMachine().getName();
        dto.operationOrder = operation.getOperationOrder();
        dto.description = operation.getDescription();
        dto.setupTimeMinutes = operation.getSetupTimeMinutes();
        dto.runTimeMinutes = operation.getRunTimeMinutes();
        return dto;
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
