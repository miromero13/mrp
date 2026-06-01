package eccomerce.backend_eccomerce.materials.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.products.entity.ProductEntity;
import eccomerce.backend_eccomerce.materials.enums.BatchStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchEntity extends BaseEntity {

    @Column(name = "batch_number", unique = true, nullable = false)
    private String batchNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private BatchStatusEnum status = BatchStatusEnum.APPROVED;

    @Column(name = "production_date")
    private LocalDate productionDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "current_stock", precision = 19, scale = 4)
    private BigDecimal currentStock = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private MaterialEntity material;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    private EnterpriseEntity enterprise;
}
