package eccomerce.backend_eccomerce.products.entity;

import java.math.BigDecimal;
import java.util.UUID;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.materials.entity.MaterialEntity;
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
@Table(name = "product_material")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductMaterialEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_material_product"))
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_material_material"))
    private MaterialEntity material;

    @Column(name = "quantity", precision = 19, scale = 4, nullable = false)
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(name = "is_active")
    private Boolean isActive = true;
}