package eccomerce.backend_eccomerce.work_shift.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import eccomerce.backend_eccomerce.user.entity.UserEntity;

@Entity
@Table(name = "employee_shifts")
@Setter
@Getter
public class EmployeeShiftEntity extends BaseEntity {
    private String dayOfWeek;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "work_shift_id", nullable = false)
    private WorkShiftEntity workShift;
}
