package eccomerce.backend_eccomerce.users.controller;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.users.dto.AuthLoginResponseDto;
import eccomerce.backend_eccomerce.users.dto.UserSessionDto;
import eccomerce.backend_eccomerce.users.dto.UserLoginRequestDto;
import eccomerce.backend_eccomerce.users.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseMessage<AuthLoginResponseDto> login(@Valid @RequestBody UserLoginRequestDto loginRequestDto) {
        return authService.authenticateUser(loginRequestDto);
    }

    @GetMapping("/session")
    public ResponseMessage<UserSessionDto> session(Authentication authentication) {
        return authService.getCurrentSession(authentication.getName());
    }
}
