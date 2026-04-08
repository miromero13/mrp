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
    public String nombre;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "permiso_rol",
        joinColumns = @JoinColumn(name = "rol_id"),
        inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    public Set<PermissionEntity> permisos = new HashSet<>();
}
