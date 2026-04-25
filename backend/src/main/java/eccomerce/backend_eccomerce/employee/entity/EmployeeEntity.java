package eccomerce.backend_eccomerce.employee.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.user.entity.UserEntity;
import eccomerce.backend_eccomerce.work_shift.entity.EmployeeShiftEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employees")
@Getter
@Setter
public class EmployeeEntity extends BaseEntity {
    private String internalCode;
    private String position;
    private BigDecimal hourlyRate;
    private LocalDate hireDate;
    private Boolean status;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /*@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<EmployeeShiftEntity> shifts;*/
}
