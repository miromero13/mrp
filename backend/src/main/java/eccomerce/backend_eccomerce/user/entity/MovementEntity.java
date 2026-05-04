package eccomerce.backend_eccomerce.user.entity;
import eccomerce.backend_eccomerce.common.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Data
@Entity
@Table(name = "material_movements")
@EqualsAndHashCode(callSuper = true)
public class MovementEntity extends BaseEntity {

    public enum MovementType {
        ENTRADA, SALIDA
    }
    
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 10)
    private MovementType movementType;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private MaterialEntity material;
}