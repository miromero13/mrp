package eccomerce.backend_eccomerce.work_shift.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.employee.entity.EmployeeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employee_shifts")
@Setter
@Getter
public class EmployeeShiftEntity extends BaseEntity {
    private String dayOfWeek;

   /* @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employee;

    @ManyToOne
    @JoinColumn(name = "work_shift_id")
    private WorkShiftEntity workShift;*/
}
