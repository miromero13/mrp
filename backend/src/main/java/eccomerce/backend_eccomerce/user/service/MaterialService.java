package eccomerce.backend_eccomerce.user.service;

import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.user.dto.CreateMaterialDto;
import eccomerce.backend_eccomerce.user.dto.UpdateMaterialDto;
import eccomerce.backend_eccomerce.user.entity.MaterialEntity;
import eccomerce.backend_eccomerce.user.repository.MaterialRepository;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    /**
     * Crea un nuevo material en el area de inventario
     * @param createMaterialDto
     * @return
     */
    public ResponseMessage<MaterialEntity> createMaterial(CreateMaterialDto createMaterialDto) {
        try {
            MaterialEntity material = new MaterialEntity();
            material.setCode(createMaterialDto.getCode());
            material.setName(createMaterialDto.getName());
            material.setDescription(createMaterialDto.getDescription());
            material.setMeasureUnit(createMaterialDto.getMeasureUnit());
            material.setCurrentStock(createMaterialDto.getCurrentStock());
            material.setMinimumStock(createMaterialDto.getMinimumStock());
            material.setState(createMaterialDto.getState());
            materialRepository.save(material);

            return ResponseMessage.success(material, "Material creado correctamente", 1);
        } catch (DataIntegrityViolationException ex) {            
            return ResponseMessage.error("No se pudo crear el material", "Violacion de integridad de datos: " + ex.getMostSpecificCause().getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo crear el material", "Ocurrio un error al crear el material: " + ex.getMessage(), 500);
        }
    }

    /**
     * Actualiza un material existente en el area de inventario
     * @param id
     * @param updateMaterialDto
     * @return
     */
    public ResponseMessage<MaterialEntity> updateMaterial(@NonNull UUID id, UpdateMaterialDto updateMaterialDto) {
        try {
            MaterialEntity material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con id: " + id));

            if (updateMaterialDto.getName() != null && !updateMaterialDto.getName().isEmpty()) {
                material.setName(updateMaterialDto.getName());
            }
            if (updateMaterialDto.getDescription() != null && !updateMaterialDto.getDescription().isEmpty()) {
                material.setDescription(updateMaterialDto.getDescription());
            }
            if (updateMaterialDto.getMeasureUnit() != null && !updateMaterialDto.getMeasureUnit().isEmpty()) {
                material.setMeasureUnit(updateMaterialDto.getMeasureUnit());
            }
            if (updateMaterialDto.getCurrentStock() != null) {
                material.setCurrentStock(updateMaterialDto.getCurrentStock());
            }
            if (updateMaterialDto.getMinimumStock() != null) {
                material.setMinimumStock(updateMaterialDto.getMinimumStock());
            }
            materialRepository.save(material);

            return ResponseMessage.success(material, "Material actualizado correctamente", 1);
        } catch (RuntimeException ex) {            
            return ResponseMessage.error("No se pudo actualizar el material", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo actualizar el material", "Ocurrio un error al actualizar el material: " + ex.getMessage(), 500);
        }
    }

    /**
     * Obtiene todos los materiales del area de inventario
     * @return
     */
    public ResponseMessage<List<MaterialEntity>> getAllMaterials() {
        try {
            List<MaterialEntity> materials = materialRepository.findAll();
            return ResponseMessage.success(materials, "Materiales obtenidos correctamente", materials.size());
        } catch (Exception ex) {            
            return ResponseMessage.error("No se pudieron obtener los materiales", "Ocurrio un error al consultar materiales: " + ex.getMessage(), 500);
        }
    }

    /**
     * Elimina un material del area de inventario (cambia su estado a inactivo)
     * @param id
     * @return
     */
    public ResponseMessage<Void> deleteMaterial(@NonNull UUID id) {
        try {
            MaterialEntity material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con id: " + id));

            material.setState(false);
            materialRepository.save(material);
            return ResponseMessage.success(null, "Material eliminado correctamente", null);
        } catch (RuntimeException ex) {            
            return ResponseMessage.error("No se pudo eliminar el material", ex.getMessage(), 404);
        } catch (Exception ex) {            
            return ResponseMessage.error("No se pudo eliminar el material", "Ocurrio un error al eliminar el material: " + ex.getMessage(), 500);
        }
    }

    /**
     * Obtiene un material por su ID
     * @param id
     * @return
     */
    public ResponseMessage<MaterialEntity> getMaterialById(@NonNull UUID id) {
        try {
            MaterialEntity material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con id: " + id));

            return ResponseMessage.success(material, "Material encontrado", 1);
        } catch (RuntimeException ex) {            
            return ResponseMessage.error("No se pudo obtener el material", ex.getMessage(), 404);
        } catch (Exception ex) {            
            return ResponseMessage.error("No se pudo obtener el material", "Ocurrio un error al consultar el material: " + ex.getMessage(), 500);
        }
    }
}