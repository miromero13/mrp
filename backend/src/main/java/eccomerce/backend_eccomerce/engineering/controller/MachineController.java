package eccomerce.backend_eccomerce.engineering.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.engineering.dto.CreateMachineDto;
import eccomerce.backend_eccomerce.engineering.dto.UpdateMachineDto;
import eccomerce.backend_eccomerce.engineering.entity.MachineEntity;
import eccomerce.backend_eccomerce.engineering.service.MachineService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enterprise/machines")
@Tag(name = "Enterprise Machines")
public class MachineController {
    @Autowired
    private MachineService machineService;

    @RequirePermission(PermissionConstants.LISTAR_MAQUINARIA)
    @GetMapping
    public ResponseMessage<List<MachineEntity>> findAll() {
        return machineService.findAll();
    }

    @RequirePermission(PermissionConstants.CREAR_MAQUINARIA)
    @PostMapping
    public ResponseMessage<MachineEntity> create(@RequestBody CreateMachineDto dto) {
        return machineService.create(dto);
    }

    @RequirePermission(PermissionConstants.LISTAR_MAQUINARIA)
    @GetMapping("/{id}")
    public ResponseMessage<MachineEntity> findById(@PathVariable UUID id) {
        return machineService.findById(id);
    }

    @RequirePermission(PermissionConstants.EDITAR_MAQUINARIA)
    @PutMapping("/{id}")
    public ResponseMessage<MachineEntity> update(@PathVariable UUID id, @RequestBody UpdateMachineDto dto) {
        return machineService.update(id, dto);
    }

    @RequirePermission(PermissionConstants.ELIMINAR_MAQUINARIA)
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> delete(@PathVariable UUID id) {
        return machineService.delete(id);
    }
}
