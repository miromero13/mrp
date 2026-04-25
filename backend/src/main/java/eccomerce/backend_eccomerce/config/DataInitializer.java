package eccomerce.backend_eccomerce.config;

import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.constants.RoleConstants;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.finance.entity.IndirectCostEntity;
import eccomerce.backend_eccomerce.finance.repository.IndirectCostRepository;
import eccomerce.backend_eccomerce.engineering.entity.MachineEntity;
import eccomerce.backend_eccomerce.engineering.entity.ProductEntity;
import eccomerce.backend_eccomerce.engineering.entity.ProductVersionEntity;
import eccomerce.backend_eccomerce.engineering.repository.MachineRepository;
import eccomerce.backend_eccomerce.engineering.repository.ProductRepository;
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
import eccomerce.backend_eccomerce.warehouses.entity.MaterialEntity;
import eccomerce.backend_eccomerce.warehouses.entity.MaterialMovementDetailEntity;
import eccomerce.backend_eccomerce.warehouses.entity.MaterialMovementEntity;
import eccomerce.backend_eccomerce.warehouses.enums.MaterialMovementType;
import eccomerce.backend_eccomerce.warehouses.repository.MaterialMovementRepository;
import eccomerce.backend_eccomerce.warehouses.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
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

    @Autowired
    private WorkShiftRepository workShiftRepository;

    @Autowired
    private EmployeeShiftRepository employeeShiftRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialMovementRepository materialMovementRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private IndirectCostRepository indirectCostRepository;

    @Value("${superadmin.email}")
    private String superadminEmail;

    @Value("${superadmin.password}")
    private String superadminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        createPermissionsIfNotExist();
        migrateRoleNames();
        syncRolePermissions(RoleConstants.SUPERADMIN, PermissionConstants.SUPERADMIN_PERMISSION_NAMES);
        syncRolePermissions(RoleConstants.ADMIN, PermissionConstants.ADMIN_PERMISSION_NAMES);
        syncRolePermissions(RoleConstants.EMPLOYEE, PermissionConstants.EMPLOYEE_PERMISSION_NAMES);
        createSuperAdminUserIfNotExist();
        createEnterprisesAndAdmins();
        seedEnterpriseCatalog();
        createDefaultWorkShiftsIfNotExist();
        createSeedEmployeesAndShifts();
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
        RoleEntity role = getOrCreateRole(roleName, legacyRoleName(roleName));
        role.permissions = getPermissionsByNames(permissionNames);
        roleRepository.save(role);
    }

    private void migrateRoleNames() {
        migrateRoleName(RoleConstants.SUPERADMIN, legacyRoleName(RoleConstants.SUPERADMIN));
        migrateRoleName(RoleConstants.ADMIN, legacyRoleName(RoleConstants.ADMIN));
        migrateRoleName(RoleConstants.EMPLOYEE, legacyRoleName(RoleConstants.EMPLOYEE));
    }

    private void migrateRoleName(String canonicalName, String legacyName) {
        RoleEntity canonicalRole = roleRepository.findByName(canonicalName).orElse(null);
        RoleEntity legacyRole = roleRepository.findByName(legacyName).orElse(null);

        if (canonicalRole != null && legacyRole != null && !canonicalRole.getId().equals(legacyRole.getId())) {
            canonicalRole.permissions.addAll(legacyRole.permissions);
            userRepository.findAll().stream()
                    .filter(user -> user.role != null && user.role.getId().equals(legacyRole.getId()))
                    .forEach(user -> {
                        user.role = canonicalRole;
                        userRepository.save(user);
                    });
            roleRepository.delete(legacyRole);
            roleRepository.save(canonicalRole);
            return;
        }

        if (canonicalRole == null && legacyRole != null) {
            legacyRole.name = canonicalName;
            roleRepository.save(legacyRole);
            return;
        }

        if (canonicalRole == null) {
            RoleEntity role = new RoleEntity();
            role.name = canonicalName;
            roleRepository.save(role);
        }
    }

    private RoleEntity getOrCreateRole(String canonicalName, String legacyName) {
        RoleEntity canonicalRole = roleRepository.findByName(canonicalName).orElse(null);
        if (canonicalRole != null) {
            return canonicalRole;
        }

        RoleEntity legacyRole = roleRepository.findByName(legacyName).orElse(null);
        if (legacyRole != null) {
            legacyRole.name = canonicalName;
            return roleRepository.save(legacyRole);
        }

        RoleEntity role = new RoleEntity();
        role.name = canonicalName;
        return roleRepository.save(role);
    }

    private void createSuperAdminUserIfNotExist() {
        if (userRepository.findByEmail(superadminEmail).isEmpty()) {
            UserEntity superAdminUser = new UserEntity();
            superAdminUser.name = "Super Admin";
            superAdminUser.email = superadminEmail;
            superAdminUser.password = passwordEncoder.encode(superadminPassword);
            superAdminUser.role = getOrCreateRole(RoleConstants.SUPERADMIN, legacyRoleName(RoleConstants.SUPERADMIN));
            userRepository.save(superAdminUser);
        }
    }

    private void createEnterprisesAndAdmins() {
        for (EnterpriseSeed seed : enterpriseSeeds()) {
            
            EnterpriseEntity enterprise = enterpriseRepository.findByNit(seed.nit()).orElseGet(EnterpriseEntity::new);
            enterprise.setName(seed.name());
            enterprise.setNit(seed.nit());
            enterprise.setAddress(seed.address());
            enterpriseRepository.save(enterprise);

            UserEntity admin = userRepository.findByEmail(seed.adminEmail()).orElseGet(UserEntity::new);
            admin.name = seed.adminName();
            admin.email = seed.adminEmail();
            admin.password = passwordEncoder.encode(seed.adminPassword());
            admin.role = getOrCreateRole(RoleConstants.ADMIN, legacyRoleName(RoleConstants.ADMIN));
            admin.enterprise = enterprise;
            userRepository.save(admin);
        }
    }

    private void seedEnterpriseCatalog() {
        Map<String, EnterpriseSeed> seedsByName = enterpriseSeeds().stream()
                .collect(Collectors.toMap(EnterpriseSeed::name, seed -> seed, (left, right) -> left, LinkedHashMap::new));

        for (EnterpriseEntity enterprise : enterpriseRepository.findAll()) {
            EnterpriseSeed seed = seedsByName.get(enterprise.getName());
            if (seed == null) {
                continue;
            }

            Map<String, MaterialEntity> materialsByCode = createMaterialsForEnterprise(enterprise, seed.materials());
            createProductsForEnterprise(enterprise, seed.products(), materialsByCode);
            createMachinesForEnterprise(enterprise, seed.machines());
            createIndirectCostsForEnterprise(enterprise, seed.indirectCosts());
            createMaterialMovementsForEnterprise(enterprise, seed.materialMovements(), materialsByCode);
        }
    }

    private Map<String, MaterialEntity> createMaterialsForEnterprise(EnterpriseEntity enterprise, List<MaterialSeed> materials) {
        for (MaterialSeed seed : materials) {
            if (materialRepository.existsByCodeAndEnterpriseId(seed.code(), enterprise.getId())) {
                continue;
            }

            MaterialEntity material = new MaterialEntity();
            material.setCode(seed.code());
            material.setUnitOfMeasure(seed.unitOfMeasure());
            material.setStockMin(seed.stockMin());
            material.setStockCurrent(seed.stockCurrent());
            material.setEnterprise(enterprise);
            materialRepository.save(material);
        }

        return loadMaterialsByCode(enterprise);
    }

    private Map<String, MaterialEntity> loadMaterialsByCode(EnterpriseEntity enterprise) {
        return materialRepository.findByEnterpriseId(enterprise.getId()).stream()
                .collect(Collectors.toMap(MaterialEntity::getCode, material -> material, (left, right) -> left, LinkedHashMap::new));
    }

    private void createProductsForEnterprise(EnterpriseEntity enterprise, List<ProductSeed> products, Map<String, MaterialEntity> materialsByCode) {
        for (ProductSeed seed : products) {
            if (productRepository.existsByNameAndEnterpriseId(seed.name(), enterprise.getId())) {
                continue;
            }

            ProductEntity product = new ProductEntity();
            product.setName(seed.name());
            product.setDescription(seed.description());
            product.setProductionCost(seed.productionCost());
            product.setSalePrice(seed.salePrice());
            product.setEnterprise(enterprise);
            product.setMaterials(seed.materialCodes().stream()
                    .map(materialsByCode::get)
                    .filter(material -> material != null)
                    .collect(Collectors.toSet()));

            if (seed.versions() != null) {
                for (String versionName : seed.versions()) {
                    ProductVersionEntity version = new ProductVersionEntity();
                    version.setName(versionName);
                    version.setProduct(product);
                    product.getVersions().add(version);
                }
            }

            productRepository.save(product);
        }
    }

    private void createMachinesForEnterprise(EnterpriseEntity enterprise, List<MachineSeed> machines) {
        List<MachineEntity> existingMachines = machineRepository.findByEnterpriseIdOrderByCreatedAtDesc(enterprise.getId());

        for (MachineSeed seed : machines) {
            boolean exists = existingMachines.stream()
                    .anyMatch(machine -> machine.getName() != null && machine.getName().equalsIgnoreCase(seed.name()));
            if (exists) {
                continue;
            }

            MachineEntity machine = new MachineEntity();
            machine.setName(seed.name());
            machine.setDescription(seed.description());
            machine.setCost(seed.cost());
            machine.setEnterprise(enterprise);
            machineRepository.save(machine);
        }
    }

    private void createIndirectCostsForEnterprise(EnterpriseEntity enterprise, List<IndirectCostSeed> costs) {
        List<IndirectCostEntity> existingCosts = indirectCostRepository.findByEnterpriseIdOrderByCreatedAtDesc(enterprise.getId());

        for (IndirectCostSeed seed : costs) {
            boolean exists = existingCosts.stream()
                    .anyMatch(cost -> cost.getName() != null && cost.getName().equalsIgnoreCase(seed.name()));
            if (exists) {
                continue;
            }

            IndirectCostEntity cost = new IndirectCostEntity();
            cost.setName(seed.name());
            cost.setDescription(seed.description());
            cost.setAmount(seed.amount());
            cost.setEnterprise(enterprise);
            indirectCostRepository.save(cost);
        }
    }

    private void createMaterialMovementsForEnterprise(EnterpriseEntity enterprise, List<MaterialMovementSeed> movements, Map<String, MaterialEntity> materialsByCode) {
        for (MaterialMovementSeed seed : movements) {
            MaterialEntity material = materialsByCode.get(seed.materialCode());
            if (material == null || !materialMovementRepository.findByMaterialIdOrderByCreatedAtDesc(material.getId()).isEmpty()) {
                continue;
            }

            MaterialMovementEntity movement = new MaterialMovementEntity();
            movement.setMaterial(material);
            movement.setType(seed.type());
            movement.setReason(seed.reason());

            MaterialMovementDetailEntity detail = new MaterialMovementDetailEntity();
            detail.setQuantity(seed.quantity());
            detail.setUnitPrice(seed.unitPrice());
            detail.setMaterialMovement(movement);
            movement.getDetails().add(detail);

            materialMovementRepository.save(movement);
        }
    }

    private List<EnterpriseSeed> enterpriseSeeds() {
        return List.of(
                new EnterpriseSeed(
                        "Alpha Textiles",
                        "1000001",
                        "Calle 1 # 10-20",
                        "Admin Alpha",
                        "admin.alpha@mrp.local",
                        "Admin@123A",
                        List.of(
                                new MaterialSeed("ALP-TXT-ALG", "kg", new BigDecimal("120"), new BigDecimal("480"), new BigDecimal("7.50")),
                                new MaterialSeed("ALP-TXT-HIL", "kg", new BigDecimal("80"), new BigDecimal("320"), new BigDecimal("5.20")),
                                new MaterialSeed("ALP-TXT-TIN", "l", new BigDecimal("20"), new BigDecimal("90"), new BigDecimal("12.40"))
                        ),
                        List.of(
                                new ProductSeed("Camiseta Basica", "Camiseta de produccion textil inicial", new BigDecimal("18.00"), new BigDecimal("32.00"), List.of("ALP-TXT-ALG", "ALP-TXT-HIL"), List.of("V1")),
                                new ProductSeed("Sudadera Ligera", "Sudadera liviana para linea basica", new BigDecimal("28.00"), new BigDecimal("49.00"), List.of("ALP-TXT-ALG", "ALP-TXT-HIL", "ALP-TXT-TIN"), List.of("V1"))
                        ),
                        List.of(
                                new MachineSeed("Corte Textil", "Equipo de corte para rollos de tela", new BigDecimal("18500")),
                                new MachineSeed("Costura Industrial", "Maquina de costura para produccion continua", new BigDecimal("14200"))
                        ),
                        List.of(
                                new IndirectCostSeed("Luz Planta Textil", "Consumo electrico del taller", new BigDecimal("850")),
                                new IndirectCostSeed("Agua Proceso Textil", "Uso de agua en limpieza y proceso", new BigDecimal("210")),
                                new IndirectCostSeed("Arriendo Bodega Textil", "Costo fijo del espacio de almacenamiento", new BigDecimal("2400"))
                        ),
                        buildMaterialMovementSeeds(List.of(
                                new MaterialMovementSeed("ALP-TXT-ALG", MaterialMovementType.ENTRY, "Ingreso inicial de algodon para Alpha Textiles", new BigDecimal("480"), new BigDecimal("7.50")),
                                new MaterialMovementSeed("ALP-TXT-HIL", MaterialMovementType.ENTRY, "Ingreso inicial de hilo para Alpha Textiles", new BigDecimal("320"), new BigDecimal("5.20")),
                                new MaterialMovementSeed("ALP-TXT-TIN", MaterialMovementType.ENTRY, "Ingreso inicial de tinte para Alpha Textiles", new BigDecimal("90"), new BigDecimal("12.40"))
                        ))
                ),
                new EnterpriseSeed(
                        "Beta Foods",
                        "1000002",
                        "Calle 2 # 20-30",
                        "Admin Beta",
                        "admin.beta@mrp.local",
                        "Admin@123B",
                        List.of(
                                new MaterialSeed("BET-FDS-HAR", "kg", new BigDecimal("100"), new BigDecimal("450"), new BigDecimal("2.80")),
                                new MaterialSeed("BET-FDS-AZU", "kg", new BigDecimal("60"), new BigDecimal("260"), new BigDecimal("3.10")),
                                new MaterialSeed("BET-FDS-LEC", "kg", new BigDecimal("30"), new BigDecimal("140"), new BigDecimal("4.90"))
                        ),
                        List.of(
                                new ProductSeed("Galleta Artesanal", "Linea de galletas con insumos basicos", new BigDecimal("6.50"), new BigDecimal("12.50"), List.of("BET-FDS-HAR", "BET-FDS-AZU", "BET-FDS-LEC"), List.of("V1")),
                                new ProductSeed("Mezcla para Panqueque", "Premezcla lista para hornear", new BigDecimal("4.80"), new BigDecimal("9.90"), List.of("BET-FDS-HAR", "BET-FDS-LEC"), List.of("V1"))
                        ),
                        List.of(
                                new MachineSeed("Horno Industrial", "Horno para produccion en serie", new BigDecimal("22400")),
                                new MachineSeed("Mezcladora Planetaria", "Equipo de mezcla para harinas", new BigDecimal("13100"))
                        ),
                        List.of(
                                new IndirectCostSeed("Luz Planta Alimentos", "Consumo electrico del area de produccion", new BigDecimal("920")),
                                new IndirectCostSeed("Gas Natural", "Consumo de gas para hornos", new BigDecimal("680")),
                                new IndirectCostSeed("Agua Proceso Alimentos", "Agua para limpieza y operacion", new BigDecimal("260"))
                        ),
                        buildMaterialMovementSeeds(List.of(
                                new MaterialMovementSeed("BET-FDS-HAR", MaterialMovementType.ENTRY, "Ingreso inicial de harina para Beta Foods", new BigDecimal("450"), new BigDecimal("2.80")),
                                new MaterialMovementSeed("BET-FDS-AZU", MaterialMovementType.ENTRY, "Ingreso inicial de azucar para Beta Foods", new BigDecimal("260"), new BigDecimal("3.10")),
                                new MaterialMovementSeed("BET-FDS-LEC", MaterialMovementType.ENTRY, "Ingreso inicial de leche para Beta Foods", new BigDecimal("140"), new BigDecimal("4.90"))
                        ))
                ),
                new EnterpriseSeed(
                        "Gamma Logistics",
                        "1000003",
                        "Calle 3 # 30-40",
                        "Admin Gamma",
                        "admin.gamma@mrp.local",
                        "Admin@123C",
                        List.of(
                                new MaterialSeed("GAM-LGS-CAJ", "u", new BigDecimal("80"), new BigDecimal("300"), new BigDecimal("1.20")),
                                new MaterialSeed("GAM-LGS-CIN", "u", new BigDecimal("50"), new BigDecimal("220"), new BigDecimal("0.75")),
                                new MaterialSeed("GAM-LGS-ETQ", "u", new BigDecimal("100"), new BigDecimal("520"), new BigDecimal("0.35"))
                        ),
                        List.of(
                                new ProductSeed("Servicio de Empaque", "Servicio de preparacion y embalaje", new BigDecimal("3.20"), new BigDecimal("8.50"), List.of("GAM-LGS-CAJ", "GAM-LGS-CIN", "GAM-LGS-ETQ"), List.of("V1")),
                                new ProductSeed("Distribucion Express", "Servicio logistico de entrega rapida", new BigDecimal("6.80"), new BigDecimal("15.00"), List.of("GAM-LGS-CAJ", "GAM-LGS-ETQ"), List.of("V1"))
                        ),
                        List.of(
                                new MachineSeed("Montacargas", "Equipo de carga para bodega", new BigDecimal("29800")),
                                new MachineSeed("Bascula Industrial", "Control de peso para despacho", new BigDecimal("8700"))
                        ),
                        List.of(
                                new IndirectCostSeed("Combustible", "Consumo de combustible para rutas", new BigDecimal("1450")),
                                new IndirectCostSeed("Peajes", "Peajes de distribucion", new BigDecimal("390")),
                                new IndirectCostSeed("Luz Bodega", "Consumo electrico de almacen", new BigDecimal("510"))
                        ),
                        buildMaterialMovementSeeds(List.of(
                                new MaterialMovementSeed("GAM-LGS-CAJ", MaterialMovementType.ENTRY, "Ingreso inicial de cajas para Gamma Logistics", new BigDecimal("300"), new BigDecimal("1.20")),
                                new MaterialMovementSeed("GAM-LGS-CIN", MaterialMovementType.ENTRY, "Ingreso inicial de cinta para Gamma Logistics", new BigDecimal("220"), new BigDecimal("0.75")),
                                new MaterialMovementSeed("GAM-LGS-ETQ", MaterialMovementType.ENTRY, "Ingreso inicial de etiquetas para Gamma Logistics", new BigDecimal("520"), new BigDecimal("0.35"))
                        ))
                ),
                new EnterpriseSeed(
                        "Delta Works",
                        "1000004",
                        "Calle 4 # 40-50",
                        "Admin Delta",
                        "admin.delta@mrp.local",
                        "Admin@123D",
                        List.of(
                                new MaterialSeed("DEL-WKS-ACI", "kg", new BigDecimal("200"), new BigDecimal("700"), new BigDecimal("4.60")),
                                new MaterialSeed("DEL-WKS-TOR", "kg", new BigDecimal("70"), new BigDecimal("280"), new BigDecimal("8.20")),
                                new MaterialSeed("DEL-WKS-PIN", "l", new BigDecimal("40"), new BigDecimal("160"), new BigDecimal("11.30"))
                        ),
                        List.of(
                                new ProductSeed("Estructura Metalica", "Linea de estructuras soldadas", new BigDecimal("35.00"), new BigDecimal("62.00"), List.of("DEL-WKS-ACI", "DEL-WKS-TOR", "DEL-WKS-PIN"), List.of("V1")),
                                new ProductSeed("Mesa Industrial", "Mesa reforzada para uso industrial", new BigDecimal("24.00"), new BigDecimal("44.00"), List.of("DEL-WKS-ACI", "DEL-WKS-TOR"), List.of("V1"))
                        ),
                        List.of(
                                new MachineSeed("Soldadora MIG", "Equipo de soldadura para estructuras", new BigDecimal("26800")),
                                new MachineSeed("Taladro de Banco", "Maquina de perforacion para taller", new BigDecimal("9400"))
                        ),
                        List.of(
                                new IndirectCostSeed("Luz Taller", "Consumo electrico del taller metalmecanico", new BigDecimal("780")),
                                new IndirectCostSeed("Mantenimiento", "Mantenimiento preventivo de equipos", new BigDecimal("620")),
                                new IndirectCostSeed("Agua Taller", "Consumo de agua general", new BigDecimal("180"))
                        ),
                        buildMaterialMovementSeeds(List.of(
                                new MaterialMovementSeed("DEL-WKS-ACI", MaterialMovementType.ENTRY, "Ingreso inicial de acero para Delta Works", new BigDecimal("700"), new BigDecimal("4.60")),
                                new MaterialMovementSeed("DEL-WKS-TOR", MaterialMovementType.ENTRY, "Ingreso inicial de tornilleria para Delta Works", new BigDecimal("280"), new BigDecimal("8.20")),
                                new MaterialMovementSeed("DEL-WKS-PIN", MaterialMovementType.ENTRY, "Ingreso inicial de pintura para Delta Works", new BigDecimal("160"), new BigDecimal("11.30"))
                        ))
                ),
                new EnterpriseSeed(
                        "Omega Services",
                        "1000005",
                        "Calle 5 # 50-60",
                        "Admin Omega",
                        "admin.omega@mrp.local",
                        "Admin@123E",
                        List.of(
                                new MaterialSeed("OMG-SVC-PAP", "caja", new BigDecimal("40"), new BigDecimal("180"), new BigDecimal("0.95")),
                                new MaterialSeed("OMG-SVC-TON", "u", new BigDecimal("20"), new BigDecimal("90"), new BigDecimal("18.50")),
                                new MaterialSeed("OMG-SVC-CAR", "u", new BigDecimal("60"), new BigDecimal("250"), new BigDecimal("1.60"))
                        ),
                        List.of(
                                new ProductSeed("Servicio de Impresion", "Servicio administrado de impresion", new BigDecimal("7.20"), new BigDecimal("16.00"), List.of("OMG-SVC-PAP", "OMG-SVC-TON"), List.of("V1")),
                                new ProductSeed("Archivo Documental", "Organizacion y archivo de documentos", new BigDecimal("5.40"), new BigDecimal("12.00"), List.of("OMG-SVC-PAP", "OMG-SVC-CAR"), List.of("V1"))
                        ),
                        List.of(
                                new MachineSeed("Impresora Multifuncional", "Equipo principal de impresion", new BigDecimal("15400")),
                                new MachineSeed("Escaner Profesional", "Equipo para digitalizacion documental", new BigDecimal("11200"))
                        ),
                        List.of(
                                new IndirectCostSeed("Internet", "Conectividad para servicios digitales", new BigDecimal("430")),
                                new IndirectCostSeed("Luz Oficina", "Consumo electrico de oficina", new BigDecimal("380")),
                                new IndirectCostSeed("Agua Oficina", "Consumo de agua general", new BigDecimal("120"))
                        ),
                        buildMaterialMovementSeeds(List.of(
                                new MaterialMovementSeed("OMG-SVC-PAP", MaterialMovementType.ENTRY, "Ingreso inicial de papel para Omega Services", new BigDecimal("180"), new BigDecimal("0.95")),
                                new MaterialMovementSeed("OMG-SVC-TON", MaterialMovementType.ENTRY, "Ingreso inicial de toner para Omega Services", new BigDecimal("90"), new BigDecimal("18.50")),
                                new MaterialMovementSeed("OMG-SVC-CAR", MaterialMovementType.ENTRY, "Ingreso inicial de carpetas para Omega Services", new BigDecimal("250"), new BigDecimal("1.60"))
                        ))
                )
        );
    }

    private List<MaterialMovementSeed> buildMaterialMovementSeeds(List<MaterialMovementSeed> movements) {
        return new ArrayList<>(movements);
    }

    private void createDefaultWorkShiftsIfNotExist() {
        for (EnterpriseEntity enterprise : enterpriseRepository.findAll()) {
            createWorkShiftIfNotExists(enterprise, enterprise.getName() + " - Mañana", LocalDateTime.of(2026, 4, 24, 8, 0), LocalDateTime.of(2026, 4, 24, 12, 0));
            createWorkShiftIfNotExists(enterprise, enterprise.getName() + " - Tarde", LocalDateTime.of(2026, 4, 24, 14, 0), LocalDateTime.of(2026, 4, 24, 18, 0));
            createWorkShiftIfNotExists(enterprise, enterprise.getName() + " - Noche", LocalDateTime.of(2026, 4, 24, 20, 0), LocalDateTime.of(2026, 4, 25, 0, 0));
        }
    }

    private void createWorkShiftIfNotExists(EnterpriseEntity enterprise, String name, LocalDateTime startDate, LocalDateTime endDate) {
        if (workShiftRepository.existsByNameAndEnterpriseId(name, enterprise.getId())) {
            return;
        }

        WorkShiftEntity workShift = new WorkShiftEntity();
        workShift.setName(name);
        workShift.setStartDate(startDate);
        workShift.setEndDate(endDate);
        workShift.setEnterprise(enterprise);
        workShiftRepository.save(workShift);
    }

    private void createSeedEmployeesAndShifts() {
        for (EnterpriseEntity enterprise : enterpriseRepository.findAll()) {
            RoleEntity employeeRole = getOrCreateRole(RoleConstants.EMPLOYEE, legacyRoleName(RoleConstants.EMPLOYEE));
            Map<String, WorkShiftEntity> shiftsByName = workShiftRepository.findByEnterpriseId(enterprise.getId()).stream()
                    .collect(Collectors.toMap(WorkShiftEntity::getName, shift -> shift, (left, right) -> left, LinkedHashMap::new));

            List<EmployeeSeed> employees = List.of(
                    new EmployeeSeed(enterprise.getName() + " - Operario Mañana", "empleado." + enterprise.getNit() + ".1@mrp.local", "MONDAY", enterprise.getName() + " - Mañana"),
                    new EmployeeSeed(enterprise.getName() + " - Operario Tarde", "empleado." + enterprise.getNit() + ".2@mrp.local", "TUESDAY", enterprise.getName() + " - Tarde"),
                    new EmployeeSeed(enterprise.getName() + " - Operario Noche", "empleado." + enterprise.getNit() + ".3@mrp.local", "WEDNESDAY", enterprise.getName() + " - Noche")
            );

            for (EmployeeSeed seed : employees) {
                UserEntity employee = userRepository.findByEmail(seed.email()).orElseGet(UserEntity::new);
                employee.name = seed.name();
                employee.email = seed.email();
                employee.password = passwordEncoder.encode("Employee@123");
                employee.role = employeeRole;
                employee.enterprise = enterprise;
                employee = userRepository.save(employee);

                WorkShiftEntity shift = shiftsByName.get(seed.shiftName());
                if (shift == null) {
                    continue;
                }

                if (employeeShiftRepository.existsByUserIdAndDayOfWeek(employee.getId(), seed.dayOfWeek())) {
                    continue;
                }

                EmployeeShiftEntity assignment = new EmployeeShiftEntity();
                assignment.setUser(employee);
                assignment.setWorkShift(shift);
                assignment.setDayOfWeek(seed.dayOfWeek());
                employeeShiftRepository.save(assignment);
            }
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

    private record EnterpriseSeed(
            String name,
            String nit,
            String address,
            String adminName,
            String adminEmail,
            String adminPassword,
            List<MaterialSeed> materials,
            List<ProductSeed> products,
            List<MachineSeed> machines,
            List<IndirectCostSeed> indirectCosts,
            List<MaterialMovementSeed> materialMovements
    ) {}

    private record MaterialSeed(String code, String unitOfMeasure, BigDecimal stockMin, BigDecimal stockCurrent, BigDecimal unitPrice) {}

    private record ProductSeed(String name, String description, BigDecimal productionCost, BigDecimal salePrice, List<String> materialCodes, List<String> versions) {}

    private record MachineSeed(String name, String description, BigDecimal cost) {}

    private record IndirectCostSeed(String name, String description, BigDecimal amount) {}

    private record MaterialMovementSeed(String materialCode, MaterialMovementType type, String reason, BigDecimal quantity, BigDecimal unitPrice) {}

    private record EmployeeSeed(String name, String email, String dayOfWeek, String shiftName) {}

    private String legacyRoleName(String canonicalName) {
        if (RoleConstants.SUPERADMIN.equals(canonicalName)) {
            return "superadmin";
        }
        if (RoleConstants.ADMIN.equals(canonicalName)) {
            return "admin";
        }
        if (RoleConstants.EMPLOYEE.equals(canonicalName)) {
            return "employee";
        }
        return canonicalName;
    }
}
