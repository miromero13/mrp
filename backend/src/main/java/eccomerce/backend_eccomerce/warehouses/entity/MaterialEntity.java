package eccomerce.backend_eccomerce.warehouses.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "materials",
        uniqueConstraints = @UniqueConstraint(name = "uk_material_code_enterprise", columnNames = {"enterprise_id", "code"})
)
@Getter
@Setter
public class MaterialEntity extends BaseEntity {
    @Column(nullable = false)
    private String code;

    @Column(name = "unit_of_measure", nullable = false)
    private String unitOfMeasure;

    @Column(name = "stock_min", nullable = false, precision = 14, scale = 2)
    private BigDecimal stockMin;

    @Column(name = "stock_current", nullable = false, precision = 14, scale = 2)
    private BigDecimal stockCurrent;

    @ManyToOne
    @JoinColumn(name = "enterprise_id", nullable = false)
    private EnterpriseEntity enterprise;
}
