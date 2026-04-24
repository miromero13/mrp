package eccomerce.backend_eccomerce.common.constants;

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

    // 🔹 EMPLOYEE
    public static final String CREAR_EMPLEADO = "crear_empleado";
    public static final String EDITAR_EMPLEADO = "editar_empleado";
    public static final String ELIMINAR_EMPLEADO = "eliminar_empleado";
    public static final String LISTAR_EMPLEADO = "listar_empleado";

    // 🔹 WORK SHIFT
    public static final String CREAR_TURNO = "crear_turno";
    public static final String EDITAR_TURNO = "editar_turno";
    public static final String ELIMINAR_TURNO = "eliminar_turno";
    public static final String LISTAR_TURNO = "listar_turno";

    // 🔹 EMPLOYEE SHIFT
    public static final String ASIGNAR_TURNO = "asignar_turno";
    public static final String ELIMINAR_ASIGNACION_TURNO = "eliminar_asignacion_turno";
    public static final String LISTAR_ASIGNACION_TURNO = "listar_asignacion_turno";

    public static final String[] NAMES = {
            // Roles
            CREAR_ROL, EDITAR_ROL, ELIMINAR_ROL, LISTAR_ROL,

            // Permisos
            CREAR_PERMISO, EDITAR_PERMISO, ELIMINAR_PERMISO, LISTAR_PERMISO,

            // Usuarios
            CREAR_USUARIO, EDITAR_USUARIO, ELIMINAR_USUARIO, LISTAR_USUARIO,

            // Empresa
            CREAR_EMPRESA, EDITAR_EMPRESA, ELIMINAR_EMPRESA, LISTAR_EMPRESA,

            // Employee
            CREAR_EMPLEADO, EDITAR_EMPLEADO, ELIMINAR_EMPLEADO, LISTAR_EMPLEADO,

            // WorkShift
            CREAR_TURNO, EDITAR_TURNO, ELIMINAR_TURNO, LISTAR_TURNO,

            // EmployeeShift
            ASIGNAR_TURNO, ELIMINAR_ASIGNACION_TURNO, LISTAR_ASIGNACION_TURNO
    };

    public static final String[] DESCRIPTIONS = {
// ROLES
            "Crear nuevos roles", "Editar roles existentes", "Eliminar roles", "Listar todos los roles",

            // PERMISOS
            "Crear nuevos permisos", "Editar permisos existentes", "Eliminar permisos", "Listar todos los permisos",

            // USUARIOS
            "Crear nuevos usuarios", "Editar usuarios existentes", "Eliminar usuarios", "Listar todos los usuarios",

            // EMPRESA
            "Crear empresa", "Editar empresa", "Eliminar empresa", "Listar empresas",

            // EMPLEADO
            "Crear empleado", "Editar empleado", "Eliminar empleado", "Listar empleados",

            // TURNOS
            "Crear turno", "Editar turno", "Eliminar turno", "Listar turnos",

            // ASIGNACION TURNOS
            "Asignar turno a empleado", "Eliminar asignacion de turno", "Listar asignaciones de turnos"
    };

    public static final String[] ADMIN_PERMISSION_NAMES = {
            // ROLES
            CREAR_ROL, EDITAR_ROL, ELIMINAR_ROL, LISTAR_ROL,

            // PERMISOS
            CREAR_PERMISO, EDITAR_PERMISO, ELIMINAR_PERMISO, LISTAR_PERMISO,

            // USUARIOS
            CREAR_USUARIO, EDITAR_USUARIO, ELIMINAR_USUARIO, LISTAR_USUARIO,

            // EMPRESA
            CREAR_EMPRESA, EDITAR_EMPRESA, ELIMINAR_EMPRESA, LISTAR_EMPRESA,

            // EMPLEADO
            CREAR_EMPLEADO, EDITAR_EMPLEADO, ELIMINAR_EMPLEADO, LISTAR_EMPLEADO,

            // TURNOS
            CREAR_TURNO, EDITAR_TURNO, ELIMINAR_TURNO, LISTAR_TURNO,

            // ASIGNACION TURNOS
            ASIGNAR_TURNO, ELIMINAR_ASIGNACION_TURNO, LISTAR_ASIGNACION_TURNO
    };
}