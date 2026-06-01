package eccomerce.backend_eccomerce.products.entity;

import java.math.BigDecimal;
import java.util.UUID;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "production_cost", precision = 19, scale = 4)
    private BigDecimal productionCost = BigDecimal.ZERO;

    @Column(name = "sale_price", precision = 19, scale = 4)
    private BigDecimal salePrice = BigDecimal.ZERO;

    @Column(name = "stock_min", precision = 19, scale = 4)
    private BigDecimal stockMin = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    private EnterpriseEntity enterprise;
}