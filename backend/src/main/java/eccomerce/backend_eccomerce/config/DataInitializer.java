package eccomerce.backend_eccomerce.config;

import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.constants.RoleConstants;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.users.entity.PermissionEntity;
import eccomerce.backend_eccomerce.users.entity.RoleEntity;
import eccomerce.backend_eccomerce.users.entity.UserEntity;
import eccomerce.backend_eccomerce.users.repository.PermissionRepository;
import eccomerce.backend_eccomerce.users.repository.RoleRepository;
import eccomerce.backend_eccomerce.users.repository.UserRepository;
import eccomerce.backend_eccomerce.users.entity.EmployeeShiftEntity;
import eccomerce.backend_eccomerce.users.entity.WorkShiftEntity;
import eccomerce.backend_eccomerce.users.repository.EmployeeShiftRepository;
import eccomerce.backend_eccomerce.users.repository.WorkShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private WorkShiftRepository workShiftRepository;

    @Autowired
    private EmployeeShiftRepository employeeShiftRepository;

    @Value("${superadmin.email}")
    private String superadminEmail;

    @Value("${superadmin.password}")
    private String superadminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        createPermissionsIfNotExist();
        syncRolePermissions(RoleConstants.SUPERADMIN, PermissionConstants.SUPERADMIN_PERMISSION_NAMES);
        syncRolePermissions(RoleConstants.ADMIN, PermissionConstants.ADMIN_PERMISSION_NAMES);
        syncRolePermissions(RoleConstants.EMPLOYEE, PermissionConstants.EMPLOYEE_PERMISSION_NAMES);
        createSuperAdminUserIfNotExist();
        createEnterprisesAndAdmins();
        createDefaultWorkShiftsIfNotExist();
        createSeedEmployeeShifts();
    }

    private void createPermissionsIfNotExist() {
        for (PermissionConstants.PermissionDefinition definition : PermissionConstants.PERMISSIONS) {
            PermissionEntity permission = permissionRepository.findByName(definition.name())
                    .orElseGet(PermissionEntity::new);

            boolean changed = false;
            if (!definition.name().equals(permission.name)) {
                permission.name = definition.name();
                changed = true;
            }

            if (!definition.description().equals(permission.description)) {
                permission.description = definition.description();
                changed = true;
            }

            if (changed) {
                permissionRepository.save(permission);
            }
        }
    }

    private void syncRolePermissions(String roleName, String[] permissionNames) {
        RoleEntity role = roleRepository.findByName(roleName).orElseGet(RoleEntity::new);
        role.name = roleName;
        role.permissions = getPermissionsByNames(permissionNames);
        roleRepository.save(role);
    }

    private void createSuperAdminUserIfNotExist() {
        if (userRepository.findByEmail(superadminEmail).isEmpty()) {
            UserEntity superAdminUser = new UserEntity();
            superAdminUser.name = "Super Admin";
            superAdminUser.email = superadminEmail;
            superAdminUser.password = passwordEncoder.encode(superadminPassword);
            superAdminUser.role = roleRepository.findByName(RoleConstants.SUPERADMIN)
                    .orElseThrow(() -> new RuntimeException("Rol superadmin no encontrado"));
            userRepository.save(superAdminUser);
        }
    }

    private void createEnterprisesAndAdmins() {
        List<EnterpriseSeed> seeds = List.of(
                new EnterpriseSeed("Alpha Textiles", "1000001", "Calle 1 # 10-20", "Admin Alpha", "admin.alpha@mrp.local", "Admin@123A"),
                new EnterpriseSeed("Beta Foods", "1000002", "Calle 2 # 20-30", "Admin Beta", "admin.beta@mrp.local", "Admin@123B"),
                new EnterpriseSeed("Gamma Logistics", "1000003", "Calle 3 # 30-40", "Admin Gamma", "admin.gamma@mrp.local", "Admin@123C"),
                new EnterpriseSeed("Delta Works", "1000004", "Calle 4 # 40-50", "Admin Delta", "admin.delta@mrp.local", "Admin@123D"),
                new EnterpriseSeed("Omega Services", "1000005", "Calle 5 # 50-60", "Admin Omega", "admin.omega@mrp.local", "Admin@123E")
        );

        for (EnterpriseSeed seed : seeds) {
            EnterpriseEntity enterprise = enterpriseRepository.findByNit(seed.nit()).orElseGet(EnterpriseEntity::new);
            enterprise.setName(seed.name());
            enterprise.setNit(seed.nit());
            enterprise.setAddress(seed.address());
            enterpriseRepository.save(enterprise);

            UserEntity admin = userRepository.findByEmail(seed.adminEmail()).orElseGet(UserEntity::new);
            admin.name = seed.adminName();
            admin.email = seed.adminEmail();
            admin.password = passwordEncoder.encode(seed.adminPassword());
            admin.role = roleRepository.findByName(RoleConstants.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Rol admin no encontrado"));
            admin.enterprise = enterprise;
            userRepository.save(admin);
        }
    }

    private void createDefaultWorkShiftsIfNotExist() {
        if (workShiftRepository.count() > 0) {
            return;
        }

        List<EnterpriseEntity> enterprises = enterpriseRepository.findAll();
        for (EnterpriseEntity enterprise : enterprises) {
            WorkShiftEntity morning = new WorkShiftEntity();
            morning.setName(enterprise.getName() + " - Mañana");
            morning.setStartDate(LocalDateTime.of(2026, 4, 24, 8, 0));
            morning.setEndDate(LocalDateTime.of(2026, 4, 24, 12, 0));
            morning.setEnterprise(enterprise);

            WorkShiftEntity afternoon = new WorkShiftEntity();
            afternoon.setName(enterprise.getName() + " - Tarde");
            afternoon.setStartDate(LocalDateTime.of(2026, 4, 24, 14, 0));
            afternoon.setEndDate(LocalDateTime.of(2026, 4, 24, 18, 0));
            afternoon.setEnterprise(enterprise);

            workShiftRepository.save(morning);
            workShiftRepository.save(afternoon);
        }
    }

    private void createSeedEmployeeShifts() {
        if (employeeShiftRepository.count() > 0) {
            return;
        }

        List<UserEntity> employees = userRepository.findAll().stream()
                .filter(user -> user.role != null && RoleConstants.EMPLOYEE.equalsIgnoreCase(user.role.name))
                .toList();

        if (employees.isEmpty()) {
            employees = enterpriseRepository.findAll().stream()
                    .map(enterprise -> {
                        UserEntity seedEmployee = new UserEntity();
                        seedEmployee.name = enterprise.getName() + " - Empleado";
                        seedEmployee.email = "empleado." + enterprise.getNit() + "@mrp.local";
                        seedEmployee.password = passwordEncoder.encode("Employee@123");
                        seedEmployee.role = roleRepository.findByName(RoleConstants.EMPLOYEE)
                                .orElseThrow(() -> new RuntimeException("Rol employee no encontrado"));
                        seedEmployee.enterprise = enterprise;
                        return userRepository.save(seedEmployee);
                    })
                    .toList();
        }

        List<WorkShiftEntity> shifts = workShiftRepository.findAll();
        if (employees.isEmpty() || shifts.isEmpty()) {
            return;
        }

        for (UserEntity employee : employees) {
            WorkShiftEntity shift = shifts.stream()
                    .filter(workShift -> workShift.getEnterprise() != null && employee.enterprise != null && workShift.getEnterprise().getId().equals(employee.enterprise.getId()))
                    .findFirst()
                    .orElse(shifts.get(0));

            EmployeeShiftEntity assignment = new EmployeeShiftEntity();
            assignment.setUser(employee);
            assignment.setWorkShift(shift);
            assignment.setDayOfWeek("MONDAY");
            employeeShiftRepository.save(assignment);
        }
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

    private record EnterpriseSeed(String name, String nit, String address, String adminName, String adminEmail, String adminPassword) {}
}
