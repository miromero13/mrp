package eccomerce.backend_eccomerce.indirectcost.entity;
import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.indirectcost.enums.DistributionCriterion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Entity
@Table(name = "indirect_cost_history")
@Getter
@Setter
public class IndirectCostHistory extends BaseEntity {
    
    @Column(name = "indirect_cost_id", nullable = false)
    private UUID indirectCostId;
    
    private BigDecimal amountPrevious;
    private BigDecimal amountNew;
    
    private LocalDate startDatePrevious;
    private LocalDate startDateNew;
    
    private LocalDate endDatePrevious;
    private LocalDate endDateNew;
    
    @Enumerated(EnumType.STRING)
    private DistributionCriterion distributionCriterionPrevious;
    
    @Enumerated(EnumType.STRING)
    private DistributionCriterion distributionCriterionNew;
    
    @Column(name = "modified_by")
    private String modifiedBy;
}
