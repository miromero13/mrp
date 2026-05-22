package eccomerce.backend_eccomerce.production.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.production.dto.CreateDemandDto;
import eccomerce.backend_eccomerce.production.dto.DemandDto;
import eccomerce.backend_eccomerce.production.dto.UpdateDemandDto;
import eccomerce.backend_eccomerce.production.service.DemandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enterprise/demands")
@Tag(name = "Demands")
public class DemandController {
    @Autowired
    private DemandService demandService;

    @RequirePermission(PermissionConstants.LISTAR_DEMANDA)
    @GetMapping
    public ResponseMessage<List<DemandDto>> findAll() {
        return demandService.findAll();
    }

    @RequirePermission(PermissionConstants.LISTAR_DEMANDA)
    @GetMapping("/{id}")
    public ResponseMessage<DemandDto> findById(@PathVariable UUID id) {
        return demandService.findById(id);
    }

    @RequirePermission(PermissionConstants.CREAR_DEMANDA)
    @PostMapping
    public ResponseMessage<DemandDto> create(@Valid @RequestBody CreateDemandDto dto) {
        return demandService.create(dto);
    }

    @RequirePermission(PermissionConstants.EDITAR_DEMANDA)
    @PutMapping("/{id}")
    public ResponseMessage<DemandDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateDemandDto dto) {
        return demandService.update(id, dto);
    }

    @RequirePermission(PermissionConstants.ELIMINAR_DEMANDA)
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> delete(@PathVariable UUID id) {
        return demandService.delete(id);
    }
}
