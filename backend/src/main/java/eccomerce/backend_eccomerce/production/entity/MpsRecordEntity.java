package eccomerce.backend_eccomerce.production.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.engineering.entity.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "mps_records",
        uniqueConstraints = @UniqueConstraint(name = "uk_mps_records_enterprise_product_mode_period", columnNames = {"enterprise_id", "product_id", "period_mode", "period_start_date"})
)
@Getter
@Setter
public class MpsRecordEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id", nullable = false)
    private EnterpriseEntity enterprise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_mode", nullable = false)
    private eccomerce.backend_eccomerce.production.enums.MpsPeriodMode periodMode;

    @Column(name = "period_start_date", nullable = false)
    private LocalDate periodStartDate;

    @Column(name = "forecast_demand", nullable = false, precision = 14, scale = 2)
    private BigDecimal forecastDemand;

    @Column(name = "allocated_demand", nullable = false, precision = 14, scale = 2)
    private BigDecimal allocatedDemand;

    @Column(name = "planned_production", nullable = false, precision = 14, scale = 2)
    private BigDecimal plannedProduction;

    @Column(name = "projected_available_balance", nullable = false, precision = 14, scale = 2)
    private BigDecimal projectedAvailableBalance;
}
