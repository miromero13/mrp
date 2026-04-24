package eccomerce.backend_eccomerce.work_shift.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AssignShiftDto {
    public UUID employeeId;
    public UUID workShiftId;
    public String dayOfWeek;
}
