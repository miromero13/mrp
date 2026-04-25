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
import eccomerce.backend_eccomerce.work_shift.entity.EmployeeShiftEntity;
import eccomerce.backend_eccomerce.work_shift.entity.WorkShiftEntity;
import eccomerce.backend_eccomerce.work_shift.repository.EmployeeShiftRepository;
import eccomerce.backend_eccomerce.work_shift.repository.WorkShiftRepository;
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
        createSuperAdminRoleIfNotExist();
        createAdminRoleIfNotExist();
        createEmployeeRoleIfNotExist();
        createSuperAdminUserIfNotExist();
        createEnterprisesAndAdmins();
        createDefaultWorkShiftsIfNotExist();
        createSeedEmployeeShifts();
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
        Set<PermissionEntity> allPermissions = new HashSet<>(permissionRepository.findAll());
        RoleEntity superAdminRole = roleRepository.findByName(RoleConstants.SUPERADMIN).orElseGet(RoleEntity::new);
        superAdminRole.name = RoleConstants.SUPERADMIN;
        superAdminRole.permissions = allPermissions;
        roleRepository.save(superAdminRole);
    }

    private void createAdminRoleIfNotExist() {
        RoleEntity adminRole = roleRepository.findByName(RoleConstants.ADMIN).orElseGet(RoleEntity::new);
        adminRole.name = RoleConstants.ADMIN;
        adminRole.permissions = getPermissionsByNames(PermissionConstants.ADMIN_PERMISSION_NAMES);
        roleRepository.save(adminRole);
    }

    private void createEmployeeRoleIfNotExist() {
        RoleEntity employeeRole = roleRepository.findByName(RoleConstants.EMPLOYEE).orElseGet(RoleEntity::new);
        employeeRole.name = RoleConstants.EMPLOYEE;
        employeeRole.permissions = getPermissionsByNames(PermissionConstants.EMPLOYEE_PERMISSION_NAMES);
        roleRepository.save(employeeRole);
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
