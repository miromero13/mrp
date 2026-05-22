package eccomerce.backend_eccomerce.warehouses.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.engineering.entity.ProductVersionEntity;
import eccomerce.backend_eccomerce.warehouses.entity.MaterialEntity;
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
@Table(name = "bom_items")
@Getter
@Setter
public class BomItemEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_version_id", nullable = false)
    @JsonIgnore
    private ProductVersionEntity productVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private MaterialEntity material;

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal quantity;

    @Column(name = "scrap_factor", nullable = false, precision = 5, scale = 2)
    private BigDecimal scrapFactor = BigDecimal.ZERO;
}
