package eccomerce.backend_eccomerce.indirectcost.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.indirectcost.enums.Currency;
import eccomerce.backend_eccomerce.indirectcost.enums.DistributionCriterion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "indirect_costs")
@Getter
@Setter
public class IndirectCost extends BaseEntity {
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private IndirectCostCategory category;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "distribution_criterion", nullable = false)
    private DistributionCriterion distributionCriterion;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "cost_center_id", nullable = false)
    private CostCenter costCenter;
    
    @Column(nullable = false)
    private Boolean active = true;

    @Version
    private Long version;
}
