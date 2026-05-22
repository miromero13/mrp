package eccomerce.backend_eccomerce.production.dto;

import eccomerce.backend_eccomerce.production.enums.MpsPeriodMode;

import java.util.UUID;

public class GenerateMpsDto {
    public UUID enterpriseId;

    public UUID productId;

    public MpsPeriodMode periodMode;

    public Integer periodCount;
}
