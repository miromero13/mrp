package eccomerce.backend_eccomerce.engineering.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.engineering.dto.CreateProductionRouteDto;
import eccomerce.backend_eccomerce.engineering.dto.ProductionRouteDto;
import eccomerce.backend_eccomerce.engineering.dto.UpdateProductionRouteDto;
import eccomerce.backend_eccomerce.engineering.service.ProductionRouteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enterprise/production-routes")
@Tag(name = "Production Routes")
public class ProductionRouteController {
    @Autowired
    private ProductionRouteService productionRouteService;

    @RequirePermission(PermissionConstants.LISTAR_RUTA_PRODUCCION)
    @GetMapping
    public ResponseMessage<List<ProductionRouteDto>> findAll(@RequestParam(required = false) UUID productVersionId) {
        return productionRouteService.findAll(productVersionId);
    }

    @RequirePermission(PermissionConstants.LISTAR_RUTA_PRODUCCION)
    @GetMapping("/{id}")
    public ResponseMessage<ProductionRouteDto> findById(@PathVariable UUID id) {
        return productionRouteService.findById(id);
    }

    @RequirePermission(PermissionConstants.CREAR_RUTA_PRODUCCION)
    @PostMapping
    public ResponseMessage<ProductionRouteDto> create(@Valid @RequestBody CreateProductionRouteDto dto) {
        return productionRouteService.create(dto);
    }

    @RequirePermission(PermissionConstants.EDITAR_RUTA_PRODUCCION)
    @PutMapping("/{id}")
    public ResponseMessage<ProductionRouteDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateProductionRouteDto dto) {
        return productionRouteService.update(id, dto);
    }

    @RequirePermission(PermissionConstants.ELIMINAR_RUTA_PRODUCCION)
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> delete(@PathVariable UUID id) {
        return productionRouteService.delete(id);
    }
}
