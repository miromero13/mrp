package eccomerce.backend_eccomerce.finance.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
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
@Table(name = "enterprise_indirect_costs")
@Getter
@Setter
public class IndirectCostEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id", nullable = false)
    private EnterpriseEntity enterprise;
}
