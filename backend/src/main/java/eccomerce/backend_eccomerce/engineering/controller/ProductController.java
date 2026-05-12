package eccomerce.backend_eccomerce.engineering.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.engineering.dto.CreateProductDto;
import eccomerce.backend_eccomerce.engineering.dto.UpdateProductDto;
import eccomerce.backend_eccomerce.engineering.entity.ProductEntity;
import eccomerce.backend_eccomerce.engineering.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enterprise/products")
@Tag(name = "Products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @RequirePermission(PermissionConstants.CREAR_PRODUCTO)
    @PostMapping
    public ResponseMessage<ProductEntity> create(@Valid @RequestBody CreateProductDto dto) {
        return productService.create(dto);
    }

    @RequirePermission(PermissionConstants.LISTAR_PRODUCTO)
    @GetMapping
    public ResponseMessage<List<ProductEntity>> findAll() {
        return productService.findAll();
    }

    @RequirePermission(PermissionConstants.LISTAR_PRODUCTO)
    @GetMapping("/{id}")
    public ResponseMessage<ProductEntity> findById(@PathVariable UUID id) {
        return productService.findById(id);
    }

    @RequirePermission(PermissionConstants.EDITAR_PRODUCTO)
    @PutMapping("/{id}")
    public ResponseMessage<ProductEntity> update(@PathVariable UUID id, @Valid @RequestBody UpdateProductDto dto) {
        return productService.update(id, dto);
    }

    @RequirePermission(PermissionConstants.ELIMINAR_PRODUCTO)
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> delete(@PathVariable UUID id) {
        return productService.delete(id);
    }
}
