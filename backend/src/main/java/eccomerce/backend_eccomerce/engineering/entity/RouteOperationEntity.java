package eccomerce.backend_eccomerce.engineering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eccomerce.backend_eccomerce.common.entity.BaseEntity;
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
@Table(name = "route_operations")
@Getter
@Setter
public class RouteOperationEntity extends BaseEntity {
    @Column(name = "operation_order", nullable = false)
    private Integer operationOrder;

    @Column(nullable = false)
    private String description;

    @Column(name = "setup_time_minutes", nullable = false, precision = 8, scale = 2)
    private BigDecimal setupTimeMinutes;

    @Column(name = "run_time_minutes", nullable = false, precision = 8, scale = 2)
    private BigDecimal runTimeMinutes;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_route_id", nullable = false)
    private ProductionRouteEntity productionRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id")
    private MachineEntity machine;
}
