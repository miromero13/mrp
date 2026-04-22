package eccomerce.backend_eccomerce.indirectcost.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.indirectcost.entity.IndirectCostCategory;
import eccomerce.backend_eccomerce.indirectcost.repository.IndirectCostCategoryRepository;
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
@RequestMapping("/indirect-cost-categories")
@Tag(name = "Indirect Cost Categories", description = "API for listing indirect cost categories")
@RequiredArgsConstructor
public class IndirectCostCategoryController {

    private final IndirectCostCategoryRepository repository;

    @RequirePermission(PermissionConstants.LISTAR_CATEGORIA_COSTO)
    @Operation(summary = "List all active indirect cost categories")
    @GetMapping
    public ResponseMessage<List<Map<String, Object>>> findAll() {
        List<Map<String, Object>> response = repository.findAll().stream()
                .filter(IndirectCostCategory::getActive)
                .map(cat -> Map.of(
                        "id", (Object)cat.getId(),
                        "name", (Object)cat.getName()
                ))
                .collect(Collectors.toList());
        return ResponseMessage.success(response, "Lista de categorías obtenida", 200);
    }
}
