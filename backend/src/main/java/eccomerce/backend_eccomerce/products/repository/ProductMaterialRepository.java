package eccomerce.backend_eccomerce.products.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eccomerce.backend_eccomerce.products.entity.ProductMaterialEntity;
import eccomerce.backend_eccomerce.products.entity.ProductEntity;

@Repository
public interface ProductMaterialRepository extends JpaRepository<ProductMaterialEntity, UUID> {

    List<ProductMaterialEntity> findByProductAndIsActiveTrue(ProductEntity product);

    @Query("SELECT pm FROM ProductMaterialEntity pm WHERE pm.product = :product AND pm.isActive = true")
    List<ProductMaterialEntity> findMaterialsByProduct(@Param("product") ProductEntity product);

    @Query("SELECT pm FROM ProductMaterialEntity pm WHERE pm.isActive = true")
    List<ProductMaterialEntity> findAllActive();
}