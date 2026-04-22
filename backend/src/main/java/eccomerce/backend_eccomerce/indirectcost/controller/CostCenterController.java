package eccomerce.backend_eccomerce.indirectcost.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.indirectcost.entity.CostCenter;
import eccomerce.backend_eccomerce.indirectcost.repository.CostCenterRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cost-centers")
@Tag(name = "Cost Centers", description = "API for listing cost centers")
@RequiredArgsConstructor
public class CostCenterController {

    private final CostCenterRepository repository;

    @RequirePermission(PermissionConstants.LISTAR_CENTRO_COSTO)
    @Operation(summary = "List all active cost centers")
    @GetMapping
    public ResponseMessage<List<Map<String, Object>>> findAll() {
        List<Map<String, Object>> response = repository.findAll().stream()
                .filter(CostCenter::getActive)
                .map(cc -> Map.of(
                        "id", (Object)cc.getId(),
                        "name", (Object)cc.getName()
                ))
                .collect(Collectors.toList());
        return ResponseMessage.success(response, "Lista de centros de costo obtenida", 200);
    }
}
