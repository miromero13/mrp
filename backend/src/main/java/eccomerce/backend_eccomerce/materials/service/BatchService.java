package eccomerce.backend_eccomerce.materials.service;

import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.materials.dto.BatchDto;
import eccomerce.backend_eccomerce.materials.dto.CreateBatchRequest;
import eccomerce.backend_eccomerce.materials.entity.BatchEntity;
import eccomerce.backend_eccomerce.materials.entity.MaterialEntity;
import eccomerce.backend_eccomerce.materials.enums.BatchStatusEnum;
import eccomerce.backend_eccomerce.products.entity.ProductEntity;
import eccomerce.backend_eccomerce.materials.repository.BatchRepository;
import eccomerce.backend_eccomerce.materials.repository.MaterialRepository;
import eccomerce.backend_eccomerce.products.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BatchService {

    private final BatchRepository batchRepository;
    private final MaterialRepository materialRepository;
    private final ProductRepository productRepository;
    private final EnterpriseRepository enterpriseRepository;

    public BatchService(BatchRepository batchRepository, MaterialRepository materialRepository, ProductRepository productRepository, EnterpriseRepository enterpriseRepository) {
        this.batchRepository = batchRepository;
        this.materialRepository = materialRepository;
        this.productRepository = productRepository;
        this.enterpriseRepository = enterpriseRepository;
    }

    public List<BatchDto> findAll() {
        return batchRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public BatchDto findById(UUID id) {
        return batchRepository.findById(id).map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
    }

    @Transactional
    public BatchDto create(CreateBatchRequest request) {
        BatchEntity entity = new BatchEntity();
        
        // Autogenerate batchNumber
        String batchNumber = "BATCH-" + System.currentTimeMillis();
        entity.setBatchNumber(batchNumber);
        
        entity.setProductionDate(request.getProductionDate());
        entity.setExpirationDate(request.getExpirationDate());
        entity.setCurrentStock(request.getCurrentStock());

        if (request.getStatus() != null) {
            entity.setStatus(BatchStatusEnum.valueOf(request.getStatus()));
        }

        if (request.getMaterialId() != null) {
            MaterialEntity material = materialRepository.findById(request.getMaterialId())
                    .orElseThrow(() -> new RuntimeException("Material not found"));
            entity.setMaterial(material);
        } else if (request.getProductId() != null) {
            ProductEntity product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            entity.setProduct(product);
        }

        if (request.getEnterpriseId() != null) {
            EnterpriseEntity enterprise = enterpriseRepository.findById(request.getEnterpriseId())
                    .orElseThrow(() -> new RuntimeException("Enterprise not found"));
            entity.setEnterprise(enterprise);
        }

        BatchEntity saved = batchRepository.save(entity);
        return mapToDto(saved);
    }

    @Transactional
    public BatchDto update(UUID id, CreateBatchRequest request) {
        BatchEntity entity = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        entity.setProductionDate(request.getProductionDate());
        entity.setExpirationDate(request.getExpirationDate());
        entity.setCurrentStock(request.getCurrentStock());

        if (request.getStatus() != null) {
            entity.setStatus(BatchStatusEnum.valueOf(request.getStatus()));
        }

        if (request.getMaterialId() != null) {
            MaterialEntity material = materialRepository.findById(request.getMaterialId())
                    .orElseThrow(() -> new RuntimeException("Material not found"));
            entity.setMaterial(material);
            entity.setProduct(null);
        } else if (request.getProductId() != null) {
            ProductEntity product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            entity.setProduct(product);
            entity.setMaterial(null);
        }

        if (request.getEnterpriseId() != null) {
            EnterpriseEntity enterprise = enterpriseRepository.findById(request.getEnterpriseId())
                    .orElseThrow(() -> new RuntimeException("Enterprise not found"));
            entity.setEnterprise(enterprise);
        }

        BatchEntity saved = batchRepository.save(entity);
        return mapToDto(saved);
    }

    public void delete(UUID id) {
        batchRepository.deleteById(id);
    }

    private BatchDto mapToDto(BatchEntity entity) {
        BatchDto dto = new BatchDto();
        dto.setId(entity.getId());
        dto.setBatchNumber(entity.getBatchNumber());
        dto.setProductionDate(entity.getProductionDate());
        dto.setExpirationDate(entity.getExpirationDate());
        dto.setCurrentStock(entity.getCurrentStock());
        if (entity.getStatus() != null) {
            dto.setStatus(entity.getStatus().name());
        }
        if (entity.getMaterial() != null) {
            dto.setMaterialId(entity.getMaterial().getId());
        }
        if (entity.getProduct() != null) {
            dto.setProductId(entity.getProduct().getId());
        }
        if (entity.getEnterprise() != null) {
            dto.setEnterpriseId(entity.getEnterprise().getId());
        }
        return dto;
    }
}
