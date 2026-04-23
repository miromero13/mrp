package eccomerce.backend_eccomerce.enterprise.service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.enterprise.dto.CreateEnterpriseDto;
import eccomerce.backend_eccomerce.enterprise.dto.UpdateEnterpriseDto;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EnterpriseService {

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    // CREATE
    public ResponseMessage<EnterpriseEntity> create(CreateEnterpriseDto dto) {
        try {
            EnterpriseEntity enterprise = new EnterpriseEntity();

            enterprise.setName(dto.getName());
            enterprise.setNit(dto.getNit());
            enterprise.setAddress(dto.getAddress());

            // 👇 IMPORTANTE (evita tu error actual)
            enterprise.setCreatedAt(LocalDateTime.now());
            enterprise.setUpdatedAt(LocalDateTime.now());

            enterpriseRepository.save(enterprise);

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
            List<EnterpriseEntity> enterprises = enterpriseRepository.findAll();
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
}