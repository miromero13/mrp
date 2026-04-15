package eccomerce.backend_eccomerce.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;

import java.util.HashSet;

@Entity
@Table(name = "role")
public class RoleEntity extends BaseEntity {
    @Column(unique = true, nullable = false)
    public String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "permission_role",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    public Set<PermissionEntity> permissions = new HashSet<>();
}
