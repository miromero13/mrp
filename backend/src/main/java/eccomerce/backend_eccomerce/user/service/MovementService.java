package eccomerce.backend_eccomerce.user.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

import eccomerce.backend_eccomerce.user.entity.MovementEntity;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.user.dto.CreateMovementDto;
import eccomerce.backend_eccomerce.user.entity.MaterialEntity;
import eccomerce.backend_eccomerce.user.repository.MaterialRepository;
import eccomerce.backend_eccomerce.user.repository.MovementRepository;

@Service
public class MovementService {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MovementRepository movementRepository;

    /**
     * Registra una entrada de material al inventario
     * @param materialId
     * @param createMovementDto
     */
    public ResponseMessage<Void> logRevenue(@NonNull UUID materialId, CreateMovementDto createMovementDto) {
        try {
            MaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con id: " + materialId));

            MovementEntity mov = new MovementEntity();
            material.setCurrentStock(material.getCurrentStock() + createMovementDto.getAmount());
            mov.setMovementType(createMovementDto.getMovementType());
            mov.setAmount(createMovementDto.getAmount());
            mov.setMaterial(material);
            movementRepository.save(mov);
            materialRepository.save(material);

            return ResponseMessage.success(null, "Se ha aumentado la cantidad del material correctamente", null);
        } catch (RuntimeException ex) {            
            return ResponseMessage.error("No se registro la entrada correctamente", ex.getMessage(), 404);
        } catch (Exception ex) {            
            return ResponseMessage.error("No se pudo registrar la entrada", "Ocurrio un error al registrar la entrada: " + ex.getMessage(), 500);
        }
    }

    /**
     * Registra una salida de material del inventario
     * @param materialId
     * @param createMovementDto
     */
    public ResponseMessage<Void> logExpense(@NonNull UUID materialId, CreateMovementDto createMovementDto) {
        try {
            MaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con id: " + materialId));
            
            if (material.getCurrentStock() < createMovementDto.getAmount()) {
                throw new RuntimeException("Stock insuficiente");
            }
            material.setCurrentStock(material.getCurrentStock() - createMovementDto.getAmount());
            MovementEntity mov = new MovementEntity();
            mov.setMovementType(createMovementDto.getMovementType());
            mov.setAmount(createMovementDto.getAmount());
            mov.setMaterial(material);
            movementRepository.save(mov);
            materialRepository.save(material);

            return ResponseMessage.success(null, "Se ha reducido la cantidad del material correctamente", null);
        } catch (RuntimeException ex) {            
            return ResponseMessage.error("No se registro la salida correctamente", ex.getMessage(), 404);
        } catch (Exception ex) {            
            return ResponseMessage.error("No se pudo registrar la salida", "Ocurrio un error al registrar la salida: " + ex.getMessage(), 500);
        }
    }
}