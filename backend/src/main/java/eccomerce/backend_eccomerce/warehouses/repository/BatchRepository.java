package eccomerce.backend_eccomerce.warehouses.repository;

import eccomerce.backend_eccomerce.warehouses.entity.BatchEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchRepository extends JpaRepository<BatchEntity, UUID> {
    boolean existsByBatchNumber(String batchNumber);

    @EntityGraph(attributePaths = {"material", "product"})
    List<BatchEntity> findByMaterialIdOrderByProductionDateDesc(UUID materialId);

    @EntityGraph(attributePaths = {"material", "product"})
    List<BatchEntity> findByMaterialIdOrderByProductionDateAsc(UUID materialId);

    @EntityGraph(attributePaths = {"material", "product"})
    List<BatchEntity> findByProductIdOrderByProductionDateDesc(UUID productId);

    @EntityGraph(attributePaths = {"material", "product"})
    Optional<BatchEntity> findById(UUID id);

    @Query("""
            select distinct b from BatchEntity b
            left join fetch b.material material
            left join fetch b.product product
            where (:enterpriseId is null or material.enterprise.id = :enterpriseId or product.enterprise.id = :enterpriseId)
            order by b.productionDate desc
            """)
    List<BatchEntity> findAllForEnterprise(@Param("enterpriseId") UUID enterpriseId);

    @EntityGraph(attributePaths = {"material", "product"})
    Optional<BatchEntity> findByIdAndBatchNumber(UUID id, String batchNumber);
}
