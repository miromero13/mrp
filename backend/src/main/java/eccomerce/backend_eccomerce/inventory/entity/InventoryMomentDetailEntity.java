package eccomerce.backend_eccomerce.inventory.entity;

import java.math.BigDecimal;
import java.util.UUID;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.materials.entity.MaterialEntity;
import eccomerce.backend_eccomerce.materials.entity.BatchEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_moment_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMomentDetailEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "inventory_moment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_moment_detail_moment"))
    private InventoryMomentEntity inventoryMoment;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_moment_detail_material"))
    private MaterialEntity material;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_moment_detail_batch"))
    private BatchEntity batch;

    @Column(name = "quantity", precision = 19, scale = 4, nullable = false)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "unit_price", precision = 19, scale = 4)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "is_input")
    private Boolean isInput = true;
}