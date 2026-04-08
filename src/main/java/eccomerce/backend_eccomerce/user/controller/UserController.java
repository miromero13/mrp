package eccomerce.backend_eccomerce.user.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.user.dto.CreateUserDto;
import eccomerce.backend_eccomerce.user.dto.UpdateUserDto;
import eccomerce.backend_eccomerce.user.entity.UserEntity;
import eccomerce.backend_eccomerce.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "API for managing users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequirePermission(PermissionConstants.CREAR_USUARIO)
    @Operation()
    @PostMapping
    public ResponseMessage<UserEntity> createUser(@RequestBody CreateUserDto createUserDto) {
        return userService.createUser(createUserDto);
    }

    @RequirePermission(PermissionConstants.EDITAR_USUARIO)
    @Operation()
    @PutMapping("/{id}")
    public ResponseMessage<UserEntity> updateUser(@PathVariable UUID id, @RequestBody UpdateUserDto updateUserDto) {
        return userService.updateUser(id, updateUserDto);
    }

    @RequirePermission(PermissionConstants.LISTAR_USUARIO)
    @Operation()
    @GetMapping("/{id}")
    public ResponseMessage<UserEntity> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @RequirePermission(PermissionConstants.ELIMINAR_USUARIO)
    @Operation()
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> deleteUser(@PathVariable UUID id) {
        return userService.deleteUser(id);
    }

    @RequirePermission(PermissionConstants.LISTAR_USUARIO)
    @Operation()
    @GetMapping
    public ResponseMessage<List<UserEntity>> getAllUsers() {
        return userService.getAllUsers();
    }
}
