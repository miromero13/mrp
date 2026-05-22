package eccomerce.backend_eccomerce.production.repository;

import eccomerce.backend_eccomerce.production.entity.MpsRecordEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MpsRecordRepository extends JpaRepository<MpsRecordEntity, UUID> {
    @EntityGraph(attributePaths = {"enterprise", "product"})
    List<MpsRecordEntity> findByEnterpriseIdAndProductIdAndPeriodModeOrderByPeriodStartDateAsc(UUID enterpriseId, UUID productId, eccomerce.backend_eccomerce.production.enums.MpsPeriodMode periodMode);

    @EntityGraph(attributePaths = {"enterprise", "product"})
    Optional<MpsRecordEntity> findByIdAndEnterpriseId(UUID id, UUID enterpriseId);

    @EntityGraph(attributePaths = {"enterprise", "product"})
    Optional<MpsRecordEntity> findByEnterpriseIdAndProductIdAndPeriodModeAndPeriodStartDate(UUID enterpriseId, UUID productId, eccomerce.backend_eccomerce.production.enums.MpsPeriodMode periodMode, LocalDate periodStartDate);

    @EntityGraph(attributePaths = {"enterprise", "product"})
    List<MpsRecordEntity> findByEnterpriseIdAndProductIdAndPeriodModeAndPeriodStartDateGreaterThanEqualOrderByPeriodStartDateAsc(UUID enterpriseId, UUID productId, eccomerce.backend_eccomerce.production.enums.MpsPeriodMode periodMode, LocalDate periodStartDate);

    @EntityGraph(attributePaths = {"enterprise", "product"})
    List<MpsRecordEntity> findByEnterpriseIdAndProductIdAndPeriodModeAndPeriodStartDateLessThanOrderByPeriodStartDateDesc(UUID enterpriseId, UUID productId, eccomerce.backend_eccomerce.production.enums.MpsPeriodMode periodMode, LocalDate periodStartDate);

    void deleteByEnterpriseIdAndProductIdAndPeriodMode(UUID enterpriseId, UUID productId, eccomerce.backend_eccomerce.production.enums.MpsPeriodMode periodMode);
}
