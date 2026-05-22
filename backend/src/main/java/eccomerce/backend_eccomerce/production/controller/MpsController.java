package eccomerce.backend_eccomerce.production.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.production.dto.GenerateMpsDto;
import eccomerce.backend_eccomerce.production.dto.MpsRecordDto;
import eccomerce.backend_eccomerce.production.dto.UpdateMpsRecordDto;
import eccomerce.backend_eccomerce.production.service.MpsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enterprise/mps")
@Tag(name = "MPS")
public class MpsController {
    @Autowired
    private MpsService mpsService;

    @RequirePermission(PermissionConstants.GESTIONAR_MPS)
    @GetMapping
    public ResponseMessage<List<MpsRecordDto>> findAll(@RequestParam UUID productId, @RequestParam(required = false) eccomerce.backend_eccomerce.production.enums.MpsPeriodMode periodMode) {
        return mpsService.findAll(productId, periodMode);
    }

    @RequirePermission(PermissionConstants.GESTIONAR_MPS)
    @PostMapping("/generate")
    public ResponseMessage<List<MpsRecordDto>> generate(@Valid @RequestBody GenerateMpsDto dto) {
        return mpsService.generate(dto);
    }

    @RequirePermission(PermissionConstants.GESTIONAR_MPS)
    @PutMapping("/{id}")
    public ResponseMessage<MpsRecordDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateMpsRecordDto dto) {
        return mpsService.update(id, dto);
    }
}
