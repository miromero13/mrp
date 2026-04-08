package eccomerce.backend_eccomerce.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.common.enums.GenderEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {
    @Column(nullable = false)
    public String name;

    @Column(nullable = true)
    public String phone;

    @Enumerated(EnumType.STRING) 
    @Column(nullable = true)
    public GenderEnum gender;

    @Column(nullable = true)
    public String address;

    @Column(nullable = false)
    public String email;

    @JsonIgnore
    @Column(nullable = false)
    public String password;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    public RoleEntity rol;

}
