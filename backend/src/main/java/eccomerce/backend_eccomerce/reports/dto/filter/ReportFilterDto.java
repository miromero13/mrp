package eccomerce.backend_eccomerce.reports.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportFilterDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID materialId;
    private String materialCode;
    private String category;
    private String status;
    private String priority;
    private String severity;
}
