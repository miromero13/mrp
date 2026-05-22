package eccomerce.backend_eccomerce.engineering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.warehouses.entity.BomItemEntity;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @Column(nullable = false)
    private boolean approved = false;

    @Column(name = "used_in_production", nullable = false)
    private boolean usedInProduction = false;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @JsonIgnore
    @OneToMany(mappedBy = "productVersion", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<BomItemEntity> bomItems = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "productVersion", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<ProductionRouteEntity> productionRoutes = new ArrayList<>();
}
