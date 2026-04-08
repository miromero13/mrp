package eccomerce.backend_eccomerce.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;

import java.util.HashSet;

@Entity
@Table(name = "permission")
public class PermissionEntity extends BaseEntity {
    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String description;

    @ManyToMany(mappedBy = "permisos")
    @JsonIgnore
    public Set<RoleEntity> roles = new HashSet<>();
}
