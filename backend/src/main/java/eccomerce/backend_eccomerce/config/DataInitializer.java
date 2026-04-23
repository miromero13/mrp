package eccomerce.backend_eccomerce.config;

import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.constants.RoleConstants;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.user.entity.PermissionEntity;
import eccomerce.backend_eccomerce.user.entity.RoleEntity;
import eccomerce.backend_eccomerce.user.entity.UserEntity;
import eccomerce.backend_eccomerce.user.repository.PermissionRepository;
import eccomerce.backend_eccomerce.user.repository.RoleRepository;
import eccomerce.backend_eccomerce.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Value("${superadmin.email}")
    private String superadminEmail;

    @Value("${superadmin.password}")
    private String superadminPassword;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Crear permisos si no existen
        createPermissionsIfNotExist();

        // Crear rol superadmin si no existe
        createSuperAdminRoleIfNotExist();

        // Crear rol admin si no existe
        createAdminRoleIfNotExist();

        // Crear empresa si no existe
        createDefaultEnterpriseIfNotExist();

        // Crear usuario superadmin si no existe
        createSuperAdminUserIfNotExist();
    }

    private void createPermissionsIfNotExist() {
        if (PermissionConstants.NAMES.length != PermissionConstants.DESCRIPTIONS.length) {
            throw new IllegalStateException("NAMES y DESCRIPTIONS deben tener la misma cantidad de elementos");
        }

        for (int i = 0; i < PermissionConstants.NAMES.length; i++) {
            if (permissionRepository.findByName(PermissionConstants.NAMES[i]).isEmpty()) {
                PermissionEntity permission = new PermissionEntity();
                permission.name = PermissionConstants.NAMES[i];
                permission.description = PermissionConstants.DESCRIPTIONS[i];
                permissionRepository.save(permission);
            }
        }
    }

    private void createSuperAdminRoleIfNotExist() {
        Optional<RoleEntity> existingSuperAdmin = roleRepository.findByName(RoleConstants.SUPERADMIN);
        Set<PermissionEntity> allPermissions = new HashSet<>(permissionRepository.findAll());

        if (existingSuperAdmin.isEmpty()) {
            RoleEntity superAdminRole = new RoleEntity();
            superAdminRole.name = RoleConstants.SUPERADMIN;
            superAdminRole.permissions = allPermissions;
            roleRepository.save(superAdminRole);
        } else {
            RoleEntity superAdminRole = existingSuperAdmin.get();
            if (!hasSamePermissions(superAdminRole.permissions, allPermissions)) {
                superAdminRole.permissions = allPermissions;
                roleRepository.save(superAdminRole);
            }
        }
    }

    private void createAdminRoleIfNotExist() {
        Optional<RoleEntity> existingAdmin = roleRepository.findByName(RoleConstants.ADMIN);
        Set<PermissionEntity> adminPermissions = getPermissionsByNames(PermissionConstants.ADMIN_PERMISSION_NAMES);

        if (existingAdmin.isEmpty()) {
            RoleEntity adminRole = new RoleEntity();
            adminRole.name = RoleConstants.ADMIN;
            adminRole.permissions = adminPermissions;
            roleRepository.save(adminRole);
        } else {
            RoleEntity adminRole = existingAdmin.get();
            if (!hasSamePermissions(adminRole.permissions, adminPermissions)) {
                adminRole.permissions = adminPermissions;
                roleRepository.save(adminRole);
            }
        }
    }

    private boolean hasSamePermissions(Set<PermissionEntity> currentPermissions, Set<PermissionEntity> expectedPermissions) {
        Set<String> currentNames = currentPermissions.stream()
                .map(permission -> permission.name)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<String> expectedNames = expectedPermissions.stream()
                .map(permission -> permission.name)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return currentNames.equals(expectedNames);
    }

    private Set<PermissionEntity> getPermissionsByNames(String[] permissionNames) {
        Set<PermissionEntity> permissions = new HashSet<>();

        for (String permissionName : permissionNames) {
            PermissionEntity permission = permissionRepository.findByName(permissionName)
                    .orElseThrow(() -> new RuntimeException("Permiso no encontrado: " + permissionName));
            permissions.add(permission);
        }

        return permissions;
    }

    private void createSuperAdminUserIfNotExist() {
        if (userRepository.findByEmail(superadminEmail).isEmpty()) {
            UserEntity superAdminUser = new UserEntity();
            superAdminUser.name = "Super Admin";
            superAdminUser.email = superadminEmail;
            superAdminUser.password = passwordEncoder.encode(superadminPassword);

            // Asignar rol superadmin
            RoleEntity superAdminRole = roleRepository.findByName(RoleConstants.SUPERADMIN)
                    .orElseThrow(() -> new RuntimeException("Rol superadmin no encontrado"));
            superAdminUser.role = superAdminRole;

            userRepository.save(superAdminUser);
        }
    }

    private void createDefaultEnterpriseIfNotExist() {

        if (enterpriseRepository.findByNit("0000001").isEmpty()) {

            EnterpriseEntity enterprise = new EnterpriseEntity();
            enterprise.setName("Default Enterprise");
            enterprise.setNit("0000001");
            enterprise.setAddress("N/A");

            enterpriseRepository.save(enterprise);
        }
    }
}
