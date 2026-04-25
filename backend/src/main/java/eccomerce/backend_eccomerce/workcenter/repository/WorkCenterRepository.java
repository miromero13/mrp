package eccomerce.backend_eccomerce.workcenter.repository;

import eccomerce.backend_eccomerce.workcenter.entity.WorkCenter;
import eccomerce.backend_eccomerce.workcenter.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkCenterRepository extends JpaRepository<WorkCenter, UUID> {

    Optional<WorkCenter> findByCode(String code);

    List<WorkCenter> findByPlant(String plant);

    List<WorkCenter> findByProductionLine(String productionLine);

    List<WorkCenter> findByStatus(StatusEnum status);

    List<WorkCenter> findByIsBottleneckTrue();

    List<WorkCenter> findByIsCriticalResourceTrue();

    @Query("SELECT wc FROM WorkCenter wc WHERE wc.status = 'ACTIVE' AND wc.plant = :plant")
    List<WorkCenter> findActiveByPlant(@Param("plant") String plant);

    @Query("SELECT wc FROM WorkCenter wc WHERE wc.isBottleneck = true OR wc.isCriticalResource = true")
    List<WorkCenter> findCriticalResources();
}
