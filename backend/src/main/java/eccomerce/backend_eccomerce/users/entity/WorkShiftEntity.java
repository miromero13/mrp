package eccomerce.backend_eccomerce.users.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "work_shifts")
@Getter
@Setter
public class WorkShiftEntity extends BaseEntity {
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // RELACIONES

    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    private EnterpriseEntity enterprise;

    /*@OneToMany(mappedBy = "workShift")
    private List<EmployeeShiftEntity> employeeShifts;*/
}
