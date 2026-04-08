package eccomerce.backend_eccomerce.user.controller;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.user.dto.UserLoginRequestDto;
import eccomerce.backend_eccomerce.user.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {

    @Autowired
    private UserAuthService authService;

    @PostMapping("/login" )
    @Operation(security = @SecurityRequirement(name = ""))
    public ResponseMessage<String> login(@Valid @RequestBody UserLoginRequestDto loginRequestDto) {
        return authService.authenticateUser(loginRequestDto);
    }
}
