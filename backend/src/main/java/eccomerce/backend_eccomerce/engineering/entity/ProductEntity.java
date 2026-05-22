package eccomerce.backend_eccomerce.engineering.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "products",
        uniqueConstraints = @UniqueConstraint(name = "uk_product_name_enterprise", columnNames = {"enterprise_id", "name"})
)
@Getter
@Setter
public class ProductEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "production_cost", nullable = false, precision = 14, scale = 2)
    private BigDecimal productionCost;

    @Column(name = "sale_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal salePrice;

    @ManyToOne
    @JoinColumn(name = "enterprise_id", nullable = false)
    private EnterpriseEntity enterprise;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVersionEntity> versions = new ArrayList<>();
}
