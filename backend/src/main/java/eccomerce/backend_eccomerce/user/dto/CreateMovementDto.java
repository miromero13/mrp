package eccomerce.backend_eccomerce.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

import eccomerce.backend_eccomerce.user.entity.MovementEntity.MovementType;

@Data
public class CreateMovementDto {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private MovementType movementType;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Double amount;

    @NotNull(message = "El ID del material es obligatorio")
    private UUID materialId;

    // --- Getters & Setters ---

    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public UUID getMaterialId() { return materialId; }
    public void setMaterialId(UUID materialId) { this.materialId = materialId; }
}
