package eccomerce.backend_eccomerce.work_shift.entity;

import eccomerce.backend_eccomerce.common.entity.BaseEntity;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    @OneToMany(mappedBy = "workShift")
    private List<EmployeeShiftEntity> employeeShifts;
}
