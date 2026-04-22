package eccomerce.backend_eccomerce.indirectcost.repository;

import eccomerce.backend_eccomerce.indirectcost.entity.CostCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CostCenterRepository extends JpaRepository<CostCenter, UUID> {
}
