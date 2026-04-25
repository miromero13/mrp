package eccomerce.backend_eccomerce.enterprise.service;

import eccomerce.backend_eccomerce.common.constants.RoleConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.enterprise.dto.CreateEnterpriseDto;
import eccomerce.backend_eccomerce.enterprise.dto.UpdateEnterpriseDto;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.users.entity.UserEntity;
import eccomerce.backend_eccomerce.users.repository.RoleRepository;
import eccomerce.backend_eccomerce.users.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EnterpriseService {

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // CREATE
    @Transactional
    public ResponseMessage<EnterpriseEntity> create(CreateEnterpriseDto dto) {
        try {
            if (dto.getAdminName() == null || dto.getAdminEmail() == null || dto.getAdminPassword() == null) {
                return ResponseMessage.error("No se pudo crear la empresa", "Debes enviar los datos del admin", 400);
            }

            EnterpriseEntity enterprise = new EnterpriseEntity();

            enterprise.setName(dto.getName());
            enterprise.setNit(dto.getNit());
            enterprise.setAddress(dto.getAddress());

            enterpriseRepository.save(enterprise);

            UserEntity admin = new UserEntity();
            admin.name = dto.getAdminName();
            admin.email = dto.getAdminEmail();
            admin.password = passwordEncoder.encode(dto.getAdminPassword());
            admin.phone = dto.getAdminPhone();
            admin.gender = dto.getAdminGender();
            admin.address = dto.getAdminAddress();
            admin.enterprise = enterprise;
            admin.role = roleRepository.findByName(RoleConstants.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Rol admin no encontrado"));

            userRepository.save(admin);

            return ResponseMessage.success(enterprise, "Empresa creada correctamente", 1);

        } catch (DataIntegrityViolationException ex) {
            return ResponseMessage.error(
                    "No se pudo crear la empresa",
                    "Violación de integridad de datos: " + ex.getMostSpecificCause().getMessage(),
                    400
            );
        } catch (Exception ex) {
            return ResponseMessage.error(
                    "No se pudo crear la empresa",
                    "Error interno: " + ex.getMessage(),
                    500
            );
        }
    }

    // GET ALL
    public ResponseMessage<List<EnterpriseEntity>> findAll() {
        try {
            if (isCurrentUserSuperadmin()) {
                List<EnterpriseEntity> enterprises = enterpriseRepository.findAll();
                return ResponseMessage.success(enterprises, "Empresas obtenidas correctamente", enterprises.size());
            }

            UUID currentEnterpriseId = getCurrentEnterpriseId();
            List<EnterpriseEntity> enterprises = currentEnterpriseId == null
                    ? List.of()
                    : enterpriseRepository.findById(currentEnterpriseId)
                    .map(List::of)
                    .orElse(List.of());
            return ResponseMessage.success(enterprises, "Empresas obtenidas correctamente", enterprises.size());
        } catch (Exception ex) {
            return ResponseMessage.error(
                    "No se pudieron obtener las empresas",
                    "Error al consultar: " + ex.getMessage(),
                    500
            );
        }
    }

    // GET BY ID
    public ResponseMessage<EnterpriseEntity> findById(UUID id) {
        try {
            EnterpriseEntity enterprise = enterpriseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada con id: " + id));

            return ResponseMessage.success(enterprise, "Empresa encontrada", 1);

        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo obtener la empresa", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error(
                    "No se pudo obtener la empresa",
                    "Error interno: " + ex.getMessage(),
                    500
            );
        }
    }

    // UPDATE
    public ResponseMessage<EnterpriseEntity> update(UUID id, UpdateEnterpriseDto dto) {
        try {
            EnterpriseEntity enterprise = enterpriseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada con id: " + id));

            if (dto.getName() != null && !dto.getName().isEmpty()) {
                enterprise.setName(dto.getName());
            }

            if (dto.getNit() != null && !dto.getNit().isEmpty()) {
                enterprise.setNit(dto.getNit());
            }

            if (dto.getAddress() != null && !dto.getAddress().isEmpty()) {
                enterprise.setAddress(dto.getAddress());
            }

            // 👇 actualización automática
            enterprise.setUpdatedAt(LocalDateTime.now());

            enterpriseRepository.save(enterprise);

            return ResponseMessage.success(enterprise, "Empresa actualizada correctamente", 1);

        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo actualizar la empresa", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error(
                    "No se pudo actualizar la empresa",
                    "Error interno: " + ex.getMessage(),
                    500
            );
        }
    }

    // DELETE
    public ResponseMessage<Void> delete(UUID id) {
        try {
            EnterpriseEntity enterprise = enterpriseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada con id: " + id));

            enterpriseRepository.delete(enterprise);

            return ResponseMessage.success(null, "Empresa eliminada correctamente", null);

        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo eliminar la empresa", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error(
                    "No se pudo eliminar la empresa",
                    "Error interno: " + ex.getMessage(),
                    500
            );
        }
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
