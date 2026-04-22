package eccomerce.backend_eccomerce.indirectcost.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.indirectcost.dto.IndirectCostRequestDto;
import eccomerce.backend_eccomerce.indirectcost.dto.IndirectCostResponseDto;
import eccomerce.backend_eccomerce.indirectcost.service.IndirectCostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/indirect-costs")
@Tag(name = "Indirect Costs", description = "API for managing indirect costs (Water, Electricity, etc.)")
@RequiredArgsConstructor
public class IndirectCostController {

    private final IndirectCostService indirectCostService;

    @RequirePermission(PermissionConstants.CREAR_COSTO_INDIRECTO)
    @Operation(summary = "Register a new indirect cost")
    @PostMapping
    public ResponseMessage<IndirectCostResponseDto> create(@Valid @RequestBody IndirectCostRequestDto dto) {
        IndirectCostResponseDto response = indirectCostService.create(dto);
        return ResponseMessage.success(response, "Costo indirecto registrado correctamente", 200);
    }

    @RequirePermission(PermissionConstants.EDITAR_COSTO_INDIRECTO)
    @Operation(summary = "Update an existing indirect cost")
    @PutMapping("/{id}")
    public ResponseMessage<IndirectCostResponseDto> update(@PathVariable UUID id, @Valid @RequestBody IndirectCostRequestDto dto) {
        IndirectCostResponseDto response = indirectCostService.update(id, dto);
        return ResponseMessage.success(response, "Costo indirecto actualizado correctamente", 200);
    }

    @RequirePermission(PermissionConstants.ELIMINAR_COSTO_INDIRECTO)
    @Operation(summary = "Deactivate an indirect cost")
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> deactivate(@PathVariable UUID id) {
        indirectCostService.deactivate(id);
        return ResponseMessage.success(null, "Costo indirecto desactivado correctamente", 200);
    }

    @RequirePermission(PermissionConstants.LISTAR_COSTO_INDIRECTO)
    @Operation(summary = "List all indirect costs")
    @GetMapping
    public ResponseMessage<List<IndirectCostResponseDto>> findAll() {
        List<IndirectCostResponseDto> response = indirectCostService.findAll();
        return ResponseMessage.success(response, "Lista de costos indirectos obtenida correctamente", 200);
    }
}
