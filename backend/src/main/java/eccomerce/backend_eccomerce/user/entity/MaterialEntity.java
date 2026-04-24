package eccomerce.backend_eccomerce.user.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;

import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

//crear la migracion en resourse/V1_bootstrap_schema.sql de manera obligatoria
@Data
@Entity
@Table(name = "materials")
@EqualsAndHashCode(callSuper = true)
public class MaterialEntity extends BaseEntity {

    @Column(unique = true, name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "measure_unit", nullable = false, length = 20)
    private String measureUnit;

    @Column(name = "current_stock", nullable = false)
    private Double currentStock;

    @Column(name = "minimum_stock", nullable = false)
    private Double minimumStock;

    @Column(name = "state", nullable = false)
    private Boolean state;
}

/* @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    } */