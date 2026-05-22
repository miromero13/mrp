package eccomerce.backend_eccomerce.production.service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.engineering.entity.ProductEntity;
import eccomerce.backend_eccomerce.engineering.repository.ProductRepository;
import eccomerce.backend_eccomerce.production.dto.CreateDemandDto;
import eccomerce.backend_eccomerce.production.dto.DemandDto;
import eccomerce.backend_eccomerce.production.dto.UpdateDemandDto;
import eccomerce.backend_eccomerce.production.entity.DemandEntity;
import eccomerce.backend_eccomerce.production.enums.DemandStatus;
import eccomerce.backend_eccomerce.production.repository.DemandRepository;
import eccomerce.backend_eccomerce.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DemandService {
    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseMessage<List<DemandDto>> findAll() {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            List<DemandDto> demands = demandRepository.findByEnterpriseIdOrderByDueDateAscCreatedAtDesc(enterprise.getId())
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseMessage.success(demands, "Demandas obtenidas correctamente", demands.size());
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudieron obtener las demandas", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudieron obtener las demandas", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<DemandDto> findById(UUID id) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            DemandEntity demand = demandRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Demanda no encontrada"));
            return ResponseMessage.success(toDto(demand), "Demanda encontrada", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo obtener la demanda", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo obtener la demanda", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<DemandDto> create(CreateDemandDto dto) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            ProductEntity product = resolveProduct(dto.productId, enterprise.getId());

            DemandEntity demand = new DemandEntity();
            demand.setEnterprise(enterprise);
            demand.setProduct(product);
            demand.setType(dto.type);
            demand.setQuantity(dto.quantity);
            demand.setDueDate(dto.dueDate);
            demand.setStatus(DemandStatus.PENDING);

            demandRepository.save(demand);
            return ResponseMessage.success(toDto(demand), "Demanda creada correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo crear la demanda", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo crear la demanda", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<DemandDto> update(UUID id, UpdateDemandDto dto) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            DemandEntity demand = demandRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Demanda no encontrada"));

            if (dto.productId != null) {
                demand.setProduct(resolveProduct(dto.productId, enterprise.getId()));
            }
            if (dto.type != null) {
                demand.setType(dto.type);
            }
            if (dto.quantity != null) {
                demand.setQuantity(dto.quantity);
            }
            if (dto.dueDate != null) {
                demand.setDueDate(dto.dueDate);
            }
            if (dto.status != null) {
                demand.setStatus(dto.status);
            }

            demandRepository.save(demand);
            return ResponseMessage.success(toDto(demand), "Demanda actualizada correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo actualizar la demanda", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo actualizar la demanda", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<Void> delete(UUID id) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            DemandEntity demand = demandRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Demanda no encontrada"));

            demand.setStatus(DemandStatus.CANCELLED);
            demandRepository.save(demand);
            return ResponseMessage.success(null, "Demanda cancelada correctamente", null);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo cancelar la demanda", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo cancelar la demanda", ex.getMessage(), 500);
        }
    }

    private ProductEntity resolveProduct(UUID productId, UUID enterpriseId) {
        return productRepository.findByIdAndEnterpriseId(productId, enterpriseId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    private DemandDto toDto(DemandEntity demand) {
        DemandDto dto = new DemandDto();
        dto.id = demand.getId();
        dto.enterpriseId = demand.getEnterprise().getId();
        dto.enterpriseName = demand.getEnterprise().getName();
        dto.productId = demand.getProduct().getId();
        dto.productName = demand.getProduct().getName();
        dto.type = demand.getType();
        dto.quantity = demand.getQuantity();
        dto.dueDate = demand.getDueDate();
        dto.status = demand.getStatus();
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
