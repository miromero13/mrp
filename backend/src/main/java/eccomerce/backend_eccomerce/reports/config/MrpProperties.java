package eccomerce.backend_eccomerce.reports.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "mrp")
public class MrpProperties {

    private Integer horizonDays = 30;
    private Integer safetyStockDays = 7;
    private Integer obsoleteDays = 30;
    private String defaultCurrency = "USD";
    private Boolean enableAutoPlanning = false;
}