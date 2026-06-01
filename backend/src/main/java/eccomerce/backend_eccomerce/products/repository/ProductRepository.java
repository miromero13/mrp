package eccomerce.backend_eccomerce.products.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eccomerce.backend_eccomerce.products.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true AND (:enterpriseId IS NULL OR p.enterprise.id = :enterpriseId)")
    List<ProductEntity> findByIsActiveTrueAndEnterprise(@Param("enterpriseId") UUID enterpriseId);

    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true AND p.stockMin > 0 AND (:enterpriseId IS NULL OR p.enterprise.id = :enterpriseId)")
    List<ProductEntity> findProductsWithMinStockAndEnterprise(@Param("enterpriseId") UUID enterpriseId);
}