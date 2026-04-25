package eccomerce.backend_eccomerce.finance.service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.finance.dto.CreateIndirectCostDto;
import eccomerce.backend_eccomerce.finance.dto.UpdateIndirectCostDto;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.finance.entity.IndirectCostEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.finance.repository.IndirectCostRepository;
import eccomerce.backend_eccomerce.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class IndirectCostService {
    @Autowired
    private IndirectCostRepository indirectCostRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ResponseMessage<IndirectCostEntity> create(CreateIndirectCostDto dto) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();

            IndirectCostEntity indirectCost = new IndirectCostEntity();
            indirectCost.setName(dto.getName());
            indirectCost.setDescription(dto.getDescription());
            indirectCost.setAmount(dto.getAmount());
            indirectCost.setEnterprise(enterprise);

            indirectCostRepository.save(indirectCost);
            return ResponseMessage.success(indirectCost, "Costo indirecto creado correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo crear el costo indirecto", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo crear el costo indirecto", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<List<IndirectCostEntity>> findAll() {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            List<IndirectCostEntity> indirectCosts = indirectCostRepository.findByEnterpriseIdOrderByCreatedAtDesc(enterprise.getId());
            return ResponseMessage.success(indirectCosts, "Costos indirectos obtenidos correctamente", indirectCosts.size());
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudieron obtener los costos indirectos", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudieron obtener los costos indirectos", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<IndirectCostEntity> findById(UUID id) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            IndirectCostEntity indirectCost = indirectCostRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Costo indirecto no encontrado"));
            return ResponseMessage.success(indirectCost, "Costo indirecto encontrado", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo obtener el costo indirecto", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo obtener el costo indirecto", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<IndirectCostEntity> update(UUID id, UpdateIndirectCostDto dto) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            IndirectCostEntity indirectCost = indirectCostRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Costo indirecto no encontrado"));

            if (dto.getName() != null && !dto.getName().isBlank()) {
                indirectCost.setName(dto.getName());
            }
            if (dto.getDescription() != null) {
                indirectCost.setDescription(dto.getDescription());
            }
            if (dto.getAmount() != null) {
                indirectCost.setAmount(dto.getAmount());
            }

            indirectCostRepository.save(indirectCost);
            return ResponseMessage.success(indirectCost, "Costo indirecto actualizado correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo actualizar el costo indirecto", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo actualizar el costo indirecto", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<Void> delete(UUID id) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            IndirectCostEntity indirectCost = indirectCostRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Costo indirecto no encontrado"));

            indirectCostRepository.delete(indirectCost);
            return ResponseMessage.success(null, "Costo indirecto eliminado correctamente", null);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo eliminar el costo indirecto", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo eliminar el costo indirecto", ex.getMessage(), 500);
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
