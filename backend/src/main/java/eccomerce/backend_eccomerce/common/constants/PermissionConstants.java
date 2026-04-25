package eccomerce.backend_eccomerce.common.constants;

import java.util.Arrays;

public final class PermissionConstants {

    private PermissionConstants() {
    }

    public static final String CREAR_ROL = "crear_rol";
    public static final String EDITAR_ROL = "editar_rol";
    public static final String ELIMINAR_ROL = "eliminar_rol";
    public static final String LISTAR_ROL = "listar_rol";

    public static final String CREAR_PERMISO = "crear_permiso";
    public static final String EDITAR_PERMISO = "editar_permiso";
    public static final String ELIMINAR_PERMISO = "eliminar_permiso";
    public static final String LISTAR_PERMISO = "listar_permiso";

    public static final String CREAR_USUARIO = "crear_usuario";
    public static final String EDITAR_USUARIO = "editar_usuario";
    public static final String ELIMINAR_USUARIO = "eliminar_usuario";
    public static final String LISTAR_USUARIO = "listar_usuario";

    public static final String CREAR_EMPRESA = "crear_empresa";
    public static final String EDITAR_EMPRESA = "editar_empresa";
    public static final String ELIMINAR_EMPRESA = "eliminar_empresa";
    public static final String LISTAR_EMPRESA = "listar_empresa";

        public static final String CREAR_MAQUINARIA = "crear_maquinaria";
        public static final String EDITAR_MAQUINARIA = "editar_maquinaria";
        public static final String ELIMINAR_MAQUINARIA = "eliminar_maquinaria";
        public static final String LISTAR_MAQUINARIA = "listar_maquinaria";

        public static final String CREAR_COSTO_INDIRECTO = "crear_costo_indirecto";
        public static final String EDITAR_COSTO_INDIRECTO = "editar_costo_indirecto";
        public static final String ELIMINAR_COSTO_INDIRECTO = "eliminar_costo_indirecto";
        public static final String LISTAR_COSTO_INDIRECTO = "listar_costo_indirecto";

    public static final String CREAR_EMPLEADO = "crear_empleado";
    public static final String EDITAR_EMPLEADO = "editar_empleado";
    public static final String ELIMINAR_EMPLEADO = "eliminar_empleado";
    public static final String LISTAR_EMPLEADO = "listar_empleado";

    public static final String CREAR_TURNO = "crear_turno";
    public static final String EDITAR_TURNO = "editar_turno";
    public static final String ELIMINAR_TURNO = "eliminar_turno";
    public static final String LISTAR_TURNO = "listar_turno";

    public static final String ASIGNAR_TURNO = "asignar_turno";
    public static final String ELIMINAR_ASIGNACION_TURNO = "eliminar_asignacion_turno";
    public static final String LISTAR_ASIGNACION_TURNO = "listar_asignacion_turno";

    public static final String CREAR_MATERIAL = "crear_material";
    public static final String EDITAR_MATERIAL = "editar_material";
    public static final String ELIMINAR_MATERIAL = "eliminar_material";
    public static final String LISTAR_MATERIAL = "listar_material";
    public static final String CREAR_MOVIMIENTO_MATERIAL = "crear_movimiento_material";
    public static final String LISTAR_MOVIMIENTO_MATERIAL = "listar_movimiento_material";

    public static final String CREAR_PRODUCTO = "crear_producto";
    public static final String EDITAR_PRODUCTO = "editar_producto";
    public static final String ELIMINAR_PRODUCTO = "eliminar_producto";
    public static final String LISTAR_PRODUCTO = "listar_producto";

    public record PermissionDefinition(String name, String description) {
    }

    public static final PermissionDefinition[] PERMISSIONS = {
            new PermissionDefinition(CREAR_ROL, "Crear nuevos roles"),
            new PermissionDefinition(EDITAR_ROL, "Editar roles existentes"),
            new PermissionDefinition(ELIMINAR_ROL, "Eliminar roles"),
            new PermissionDefinition(LISTAR_ROL, "Listar todos los roles"),

            new PermissionDefinition(CREAR_PERMISO, "Crear nuevos permisos"),
            new PermissionDefinition(EDITAR_PERMISO, "Editar permisos existentes"),
            new PermissionDefinition(ELIMINAR_PERMISO, "Eliminar permisos"),
            new PermissionDefinition(LISTAR_PERMISO, "Listar todos los permisos"),

            new PermissionDefinition(CREAR_USUARIO, "Crear nuevos usuarios"),
            new PermissionDefinition(EDITAR_USUARIO, "Editar usuarios existentes"),
            new PermissionDefinition(ELIMINAR_USUARIO, "Eliminar usuarios"),
            new PermissionDefinition(LISTAR_USUARIO, "Listar todos los usuarios"),

            new PermissionDefinition(CREAR_EMPRESA, "Crear empresa"),
            new PermissionDefinition(EDITAR_EMPRESA, "Editar empresa"),
            new PermissionDefinition(ELIMINAR_EMPRESA, "Eliminar empresa"),
            new PermissionDefinition(LISTAR_EMPRESA, "Listar empresas"),

            new PermissionDefinition(CREAR_MAQUINARIA, "Crear maquinaria"),
            new PermissionDefinition(EDITAR_MAQUINARIA, "Editar maquinaria"),
            new PermissionDefinition(ELIMINAR_MAQUINARIA, "Eliminar maquinaria"),
            new PermissionDefinition(LISTAR_MAQUINARIA, "Listar maquinaria"),

            new PermissionDefinition(CREAR_COSTO_INDIRECTO, "Crear costo indirecto"),
            new PermissionDefinition(EDITAR_COSTO_INDIRECTO, "Editar costo indirecto"),
            new PermissionDefinition(ELIMINAR_COSTO_INDIRECTO, "Eliminar costo indirecto"),
            new PermissionDefinition(LISTAR_COSTO_INDIRECTO, "Listar costos indirectos"),

            new PermissionDefinition(CREAR_EMPLEADO, "Crear empleado"),
            new PermissionDefinition(EDITAR_EMPLEADO, "Editar empleado"),
            new PermissionDefinition(ELIMINAR_EMPLEADO, "Eliminar empleado"),
            new PermissionDefinition(LISTAR_EMPLEADO, "Listar empleados"),

            new PermissionDefinition(CREAR_TURNO, "Crear turno"),
            new PermissionDefinition(EDITAR_TURNO, "Editar turno"),
            new PermissionDefinition(ELIMINAR_TURNO, "Eliminar turno"),
            new PermissionDefinition(LISTAR_TURNO, "Listar turnos"),

            new PermissionDefinition(ASIGNAR_TURNO, "Asignar turno a empleado"),
            new PermissionDefinition(ELIMINAR_ASIGNACION_TURNO, "Eliminar asignacion de turno"),
            new PermissionDefinition(LISTAR_ASIGNACION_TURNO, "Listar asignaciones de turnos"),

            new PermissionDefinition(CREAR_MATERIAL, "Crear material"),
            new PermissionDefinition(EDITAR_MATERIAL, "Editar material"),
            new PermissionDefinition(ELIMINAR_MATERIAL, "Eliminar material"),
            new PermissionDefinition(LISTAR_MATERIAL, "Listar materiales"),
            new PermissionDefinition(CREAR_MOVIMIENTO_MATERIAL, "Registrar movimiento de material"),
            new PermissionDefinition(LISTAR_MOVIMIENTO_MATERIAL, "Listar movimientos de material"),

            new PermissionDefinition(CREAR_PRODUCTO, "Crear producto"),
            new PermissionDefinition(EDITAR_PRODUCTO, "Editar producto"),
            new PermissionDefinition(ELIMINAR_PRODUCTO, "Eliminar producto"),
            new PermissionDefinition(LISTAR_PRODUCTO, "Listar productos")
    };

    private static final String[] ROLE_PERMISSION_NAMES = {
            CREAR_ROL, EDITAR_ROL, ELIMINAR_ROL, LISTAR_ROL
    };

    private static final String[] PERMISSION_PERMISSION_NAMES = {
            CREAR_PERMISO, EDITAR_PERMISO, ELIMINAR_PERMISO, LISTAR_PERMISO
    };

    private static final String[] USER_PERMISSION_NAMES = {
            CREAR_USUARIO, EDITAR_USUARIO, ELIMINAR_USUARIO, LISTAR_USUARIO
    };

    private static final String[] ENTERPRISE_PERMISSION_NAMES = {
            CREAR_EMPRESA, EDITAR_EMPRESA, ELIMINAR_EMPRESA, LISTAR_EMPRESA
    };

    private static final String[] MACHINE_PERMISSION_NAMES = {
            CREAR_MAQUINARIA, EDITAR_MAQUINARIA, ELIMINAR_MAQUINARIA, LISTAR_MAQUINARIA
    };

    private static final String[] INDIRECT_COST_PERMISSION_NAMES = {
            CREAR_COSTO_INDIRECTO, EDITAR_COSTO_INDIRECTO, ELIMINAR_COSTO_INDIRECTO, LISTAR_COSTO_INDIRECTO
    };

    private static final String[] EMPLOYEE_PERMISSION_SET_NAMES = {
            CREAR_EMPLEADO, EDITAR_EMPLEADO, ELIMINAR_EMPLEADO, LISTAR_EMPLEADO
    };

    private static final String[] WORK_SHIFT_PERMISSION_NAMES = {
            CREAR_TURNO, EDITAR_TURNO, ELIMINAR_TURNO, LISTAR_TURNO
    };

    private static final String[] EMPLOYEE_SHIFT_PERMISSION_SET_NAMES = {
            ASIGNAR_TURNO, ELIMINAR_ASIGNACION_TURNO, LISTAR_ASIGNACION_TURNO
    };

    private static final String[] MATERIAL_PERMISSION_NAMES = {
            CREAR_MATERIAL, EDITAR_MATERIAL, ELIMINAR_MATERIAL, LISTAR_MATERIAL,
            CREAR_MOVIMIENTO_MATERIAL, LISTAR_MOVIMIENTO_MATERIAL
    };

    private static final String[] PRODUCT_PERMISSION_NAMES = {
            CREAR_PRODUCTO, EDITAR_PRODUCTO, ELIMINAR_PRODUCTO, LISTAR_PRODUCTO
    };

    public static final String[] SUPERADMIN_PERMISSION_NAMES = concat(
            ROLE_PERMISSION_NAMES,
            PERMISSION_PERMISSION_NAMES,
            USER_PERMISSION_NAMES,
            ENTERPRISE_PERMISSION_NAMES,
            MACHINE_PERMISSION_NAMES,
            INDIRECT_COST_PERMISSION_NAMES,
            EMPLOYEE_PERMISSION_SET_NAMES
    );

    public static final String[] ADMIN_PERMISSION_NAMES = concat(
            ROLE_PERMISSION_NAMES,
            USER_PERMISSION_NAMES,
            ENTERPRISE_PERMISSION_NAMES,
            MACHINE_PERMISSION_NAMES,
            INDIRECT_COST_PERMISSION_NAMES,
            EMPLOYEE_PERMISSION_SET_NAMES,
            WORK_SHIFT_PERMISSION_NAMES,
            EMPLOYEE_SHIFT_PERMISSION_SET_NAMES,
            MATERIAL_PERMISSION_NAMES,
            PRODUCT_PERMISSION_NAMES
    );

    public static final String[] EMPLOYEE_PERMISSION_NAMES = {
            LISTAR_TURNO,
            LISTAR_ASIGNACION_TURNO
    };

    private static String[] concat(String[]... groups) {
        return Arrays.stream(groups)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }
}
