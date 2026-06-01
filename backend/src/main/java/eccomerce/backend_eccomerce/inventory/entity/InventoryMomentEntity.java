package eccomerce.backend_eccomerce.inventory.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_moment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMomentEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private InventoryMomentType type;

    @Column(name = "reason", length = 100)
    private String reason;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(name = "status", length = 20)
    private String status = "PENDING";

    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "inventoryMoment")
    private List<InventoryMomentDetailEntity> inventoryMomentDetails = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    private EnterpriseEntity enterprise;

    public enum InventoryMomentType {
        ENTRADA,
        SALIDA,
        AJUSTE_POSITIVO,
        AJUSTE_NEGATIVO,
        TRANSFERENCIA,
        ORDEN_COMPRA,
        ORDEN_PRODUCCION
    }
}