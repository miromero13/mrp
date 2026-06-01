package eccomerce.backend_eccomerce.materials.entity;

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
@Table(name = "materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialEntity extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "stock_current", precision = 19, scale = 4)
    private BigDecimal stockCurrent = BigDecimal.ZERO;

    @Column(name = "stock_min", precision = 19, scale = 4)
    private BigDecimal stockMin = BigDecimal.ZERO;

    @Column(name = "unit_of_measure", length = 20)
    private String unitOfMeasure;

    @Column(name = "unit_cost", precision = 19, scale = 4)
    private BigDecimal unitCost = BigDecimal.ZERO;

    @Column(name = "lead_time_days")
    private Integer leadTimeDays = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    private EnterpriseEntity enterprise;
}