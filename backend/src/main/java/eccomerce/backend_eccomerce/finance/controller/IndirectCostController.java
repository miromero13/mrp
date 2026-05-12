package eccomerce.backend_eccomerce.finance.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.finance.dto.CreateIndirectCostDto;
import eccomerce.backend_eccomerce.finance.dto.UpdateIndirectCostDto;
import eccomerce.backend_eccomerce.finance.entity.IndirectCostEntity;
import eccomerce.backend_eccomerce.finance.service.IndirectCostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enterprise/indirect-costs")
@Tag(name = "Enterprise Indirect Costs")
public class IndirectCostController {
    @Autowired
    private IndirectCostService indirectCostService;

    @RequirePermission(PermissionConstants.LISTAR_COSTO_INDIRECTO)
    @GetMapping
    public ResponseMessage<List<IndirectCostEntity>> findAll() {
        return indirectCostService.findAll();
    }

    @RequirePermission(PermissionConstants.CREAR_COSTO_INDIRECTO)
    @PostMapping
    public ResponseMessage<IndirectCostEntity> create(@RequestBody CreateIndirectCostDto dto) {
        return indirectCostService.create(dto);
    }

    @RequirePermission(PermissionConstants.LISTAR_COSTO_INDIRECTO)
    @GetMapping("/{id}")
    public ResponseMessage<IndirectCostEntity> findById(@PathVariable UUID id) {
        return indirectCostService.findById(id);
    }

    @RequirePermission(PermissionConstants.EDITAR_COSTO_INDIRECTO)
    @PutMapping("/{id}")
    public ResponseMessage<IndirectCostEntity> update(@PathVariable UUID id, @RequestBody UpdateIndirectCostDto dto) {
        return indirectCostService.update(id, dto);
    }

    @RequirePermission(PermissionConstants.ELIMINAR_COSTO_INDIRECTO)
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> delete(@PathVariable UUID id) {
        return indirectCostService.delete(id);
    }
}
