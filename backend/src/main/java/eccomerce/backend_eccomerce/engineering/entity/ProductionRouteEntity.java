package eccomerce.backend_eccomerce.engineering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "production_routes")
@Getter
@Setter
public class ProductionRouteEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_version_id", nullable = false)
    private ProductVersionEntity productVersion;

    @OrderBy("operationOrder ASC")
    @OneToMany(mappedBy = "productionRoute", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<RouteOperationEntity> operations = new ArrayList<>();
}
