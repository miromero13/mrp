package eccomerce.backend_eccomerce.enterprise.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enterprise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseEntity extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nit", unique = true, nullable = false)
    private String nit;

    @Column(name = "address")
    private String address;

}
