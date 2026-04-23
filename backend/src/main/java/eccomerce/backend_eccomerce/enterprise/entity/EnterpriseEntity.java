package eccomerce.backend_eccomerce.enterprise.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "enterprises")
public class EnterpriseEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nit;

    @Column(nullable = true)
    private String address;

    // RELACIONES
/*
    @OneToMany(mappedBy = "enterprise")
    private List<UserEntity> users;

    @OneToMany(mappedBy = "enterprise")
    private List<MaterialEntity> materials;

    @OneToMany(mappedBy = "enterprise")
    private List<WorkShiftEntity> workShifts;

    @OneToMany(mappedBy = "enterprise")
    private List<MachineEntity> machines;

    @OneToMany(mappedBy = "enterprise")
    private List<IndirectCostEntity> indirectCosts;
 */

}
