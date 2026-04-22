package eccomerce.backend_eccomerce.indirectcost.entity;
import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "indirect_cost_categories")
@Getter
@Setter
public class IndirectCostCategory extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column
    private String description;
    
    @Column(nullable = false)
    private Boolean active = true;
}
