package eccomerce.backend_eccomerce.indirectcost.repository;

import eccomerce.backend_eccomerce.indirectcost.entity.IndirectCost;
import eccomerce.backend_eccomerce.indirectcost.entity.IndirectCostCategory;
import eccomerce.backend_eccomerce.indirectcost.entity.CostCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface IndirectCostRepository extends JpaRepository<IndirectCost, UUID> {

    @Query("SELECT COUNT(ic) > 0 FROM IndirectCost ic " +
           "WHERE ic.category = :category " +
           "AND ic.costCenter = :costCenter " +
           "AND ic.active = true " +
           "AND (:id IS NULL OR ic.id <> :id) " +
           "AND ic.startDate <= :endDate " +
           "AND ic.endDate >= :startDate")
    boolean existsOverlapping(
            @Param("category") IndirectCostCategory category,
            @Param("costCenter") CostCenter costCenter,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("id") UUID id);
}
