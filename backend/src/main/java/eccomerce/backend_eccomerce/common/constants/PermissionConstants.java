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

    public static final String CREAR_COSTO_INDIRECTO = "crear_costo_indirecto";
    public static final String EDITAR_COSTO_INDIRECTO = "editar_costo_indirecto";
    public static final String ELIMINAR_COSTO_INDIRECTO = "eliminar_costo_indirecto";
    public static final String LISTAR_COSTO_INDIRECTO = "listar_costo_indirecto";

    public static final String LISTAR_CATEGORIA_COSTO = "listar_categoria_costo";
    public static final String LISTAR_CENTRO_COSTO = "listar_centro_costo";

    public static final String[] NAMES = {
            CREAR_ROL, EDITAR_ROL, ELIMINAR_ROL, LISTAR_ROL,
            CREAR_PERMISO, EDITAR_PERMISO, ELIMINAR_PERMISO, LISTAR_PERMISO,
            CREAR_USUARIO, EDITAR_USUARIO, ELIMINAR_USUARIO, LISTAR_USUARIO,
            CREAR_COSTO_INDIRECTO, EDITAR_COSTO_INDIRECTO, ELIMINAR_COSTO_INDIRECTO, LISTAR_COSTO_INDIRECTO,
            LISTAR_CATEGORIA_COSTO, LISTAR_CENTRO_COSTO
    };

    public static final String[] DESCRIPTIONS = {
            "Crear nuevos roles", "Editar roles existentes", "Eliminar roles", "Listar todos los roles",
            "Crear nuevos permisos", "Editar permisos existentes", "Eliminar permisos", "Listar todos los permisos",
            "Crear nuevos usuarios", "Editar usuarios existentes", "Eliminar usuarios", "Listar todos los usuarios",
            "Crear costos indirectos", "Editar costos indirectos", "Eliminar costos indirectos", "Listar costos indirectos",
            "Listar categorías de costos", "Listar centros de costo"
    };

    public static final String[] ADMIN_PERMISSION_NAMES = {
            CREAR_ROL, EDITAR_ROL, ELIMINAR_ROL, LISTAR_ROL,
            CREAR_USUARIO, EDITAR_USUARIO, ELIMINAR_USUARIO, LISTAR_USUARIO,
            CREAR_COSTO_INDIRECTO, EDITAR_COSTO_INDIRECTO, ELIMINAR_COSTO_INDIRECTO, LISTAR_COSTO_INDIRECTO,
            LISTAR_CATEGORIA_COSTO, LISTAR_CENTRO_COSTO
    };
}