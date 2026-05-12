package eccomerce.backend_eccomerce.engineering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_versions")
@Getter
@Setter
public class ProductVersionEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
}
