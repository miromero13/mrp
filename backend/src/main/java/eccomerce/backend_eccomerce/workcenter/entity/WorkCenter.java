package eccomerce.backend_eccomerce.workcenter.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.workcenter.enums.ResourceTypeEnum;
import eccomerce.backend_eccomerce.workcenter.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "work_centers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class WorkCenter extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String plant;

    @Column
    private String productionLine;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceTypeEnum resourceType;

    @Column(nullable = false)
    private BigDecimal capacity; // capacidad por hora

    @Column(nullable = false)
    private BigDecimal costPerHour;

    @Column(nullable = false)
    private BigDecimal targetEfficiency; // porcentaje 0-100

    @Column
    private BigDecimal currentOee; // Overall Equipment Effectiveness

    @Builder.Default
    @Column(nullable = false)
    private Boolean isBottleneck = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isCriticalResource = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEnum status = StatusEnum.ACTIVE;

    @Column
    private String calendar; // referencia a calendario

    @Column
    private LocalDate lastMaintenanceDate;

    @Column
    private LocalDate nextMaintenanceDate;
}
