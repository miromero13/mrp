package eccomerce.backend_eccomerce.warehouses.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.engineering.entity.ProductEntity;
import eccomerce.backend_eccomerce.warehouses.enums.BatchStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "batches",
        uniqueConstraints = @UniqueConstraint(name = "uk_batches_batch_number", columnNames = {"batch_number"})
)
@Getter
@Setter
public class BatchEntity extends BaseEntity {
    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private MaterialEntity material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "production_date", nullable = false)
    private LocalDate productionDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "current_stock", nullable = false, precision = 14, scale = 2)
    private BigDecimal currentStock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status;
}
