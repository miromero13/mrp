package eccomerce.backend_eccomerce.indirectcost.repository;

import eccomerce.backend_eccomerce.indirectcost.entity.IndirectCostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface IndirectCostCategoryRepository extends JpaRepository<IndirectCostCategory, UUID> {
}
