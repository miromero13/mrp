package eccomerce.backend_eccomerce.warehouses.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.warehouses.dto.BomItemDto;
import eccomerce.backend_eccomerce.warehouses.dto.CreateBomItemDto;
import eccomerce.backend_eccomerce.warehouses.dto.UpdateBomItemDto;
import eccomerce.backend_eccomerce.warehouses.service.BomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enterprise/bom-items")
@Tag(name = "BOM Items")
public class BomController {
    @Autowired
    private BomService bomService;

    @RequirePermission(PermissionConstants.LISTAR_BOM)
    @GetMapping
    public ResponseMessage<List<BomItemDto>> findByProductVersion(@RequestParam UUID productVersionId) {
        return bomService.findByProductVersion(productVersionId);
    }

    @RequirePermission(PermissionConstants.CREAR_BOM)
    @PostMapping
    public ResponseMessage<BomItemDto> create(@Valid @RequestBody CreateBomItemDto dto) {
        return bomService.create(dto);
    }

    @RequirePermission(PermissionConstants.EDITAR_BOM)
    @PutMapping("/{id}")
    public ResponseMessage<BomItemDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateBomItemDto dto) {
        return bomService.update(id, dto);
    }

    @RequirePermission(PermissionConstants.ELIMINAR_BOM)
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> delete(@PathVariable UUID id) {
        return bomService.delete(id);
    }
}
