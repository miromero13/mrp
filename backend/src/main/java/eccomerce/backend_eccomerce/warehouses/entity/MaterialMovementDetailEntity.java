package eccomerce.backend_eccomerce.warehouses.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "material_movements_detail")
@Getter
@Setter
public class MaterialMovementDetailEntity extends BaseEntity {
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_movement_id", nullable = false)
    @JsonIgnore
    private MaterialMovementEntity materialMovement;
}
