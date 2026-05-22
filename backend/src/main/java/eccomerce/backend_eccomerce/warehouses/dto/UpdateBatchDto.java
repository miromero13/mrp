package eccomerce.backend_eccomerce.warehouses.dto;

import eccomerce.backend_eccomerce.warehouses.enums.BatchStatus;

import java.time.LocalDate;

public class UpdateBatchDto {
    public LocalDate expirationDate;

    public BatchStatus status;
}
