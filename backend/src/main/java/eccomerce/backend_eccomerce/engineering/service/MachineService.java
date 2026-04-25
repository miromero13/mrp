package eccomerce.backend_eccomerce.engineering.service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.engineering.dto.CreateMachineDto;
import eccomerce.backend_eccomerce.engineering.dto.UpdateMachineDto;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.engineering.entity.MachineEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.engineering.repository.MachineRepository;
import eccomerce.backend_eccomerce.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MachineService {
    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ResponseMessage<MachineEntity> create(CreateMachineDto dto) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();

            MachineEntity machine = new MachineEntity();
            machine.setName(dto.getName());
            machine.setDescription(dto.getDescription());
            machine.setCost(dto.getCost());
            machine.setEnterprise(enterprise);

            machineRepository.save(machine);
            return ResponseMessage.success(machine, "Maquinaria creada correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo crear la maquinaria", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo crear la maquinaria", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<List<MachineEntity>> findAll() {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            List<MachineEntity> machines = machineRepository.findByEnterpriseIdOrderByCreatedAtDesc(enterprise.getId());
            return ResponseMessage.success(machines, "Maquinarias obtenidas correctamente", machines.size());
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudieron obtener las maquinarias", ex.getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudieron obtener las maquinarias", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<MachineEntity> findById(UUID id) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            MachineEntity machine = machineRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Maquinaria no encontrada"));
            return ResponseMessage.success(machine, "Maquinaria encontrada", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo obtener la maquinaria", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo obtener la maquinaria", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<MachineEntity> update(UUID id, UpdateMachineDto dto) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            MachineEntity machine = machineRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Maquinaria no encontrada"));

            if (dto.getName() != null && !dto.getName().isBlank()) {
                machine.setName(dto.getName());
            }
            if (dto.getDescription() != null) {
                machine.setDescription(dto.getDescription());
            }
            if (dto.getCost() != null) {
                machine.setCost(dto.getCost());
            }

            machineRepository.save(machine);
            return ResponseMessage.success(machine, "Maquinaria actualizada correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo actualizar la maquinaria", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo actualizar la maquinaria", ex.getMessage(), 500);
        }
    }

    @Transactional
    public ResponseMessage<Void> delete(UUID id) {
        try {
            EnterpriseEntity enterprise = resolveCurrentEnterprise();
            MachineEntity machine = machineRepository.findByIdAndEnterpriseId(id, enterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Maquinaria no encontrada"));

            machineRepository.delete(machine);
            return ResponseMessage.success(null, "Maquinaria eliminada correctamente", null);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo eliminar la maquinaria", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo eliminar la maquinaria", ex.getMessage(), 500);
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
