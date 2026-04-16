export const PERMISOS = {
  roles: {
    crear: 'crear_rol',
    editar: 'editar_rol',
    eliminar: 'eliminar_rol',
    listar: 'listar_rol',
  },
  permisos: {
    crear: 'crear_permiso',
    editar: 'editar_permiso',
    eliminar: 'eliminar_permiso',
    listar: 'listar_permiso',
  },
  usuarios: {
    crear: 'crear_usuario',
    editar: 'editar_usuario',
    eliminar: 'eliminar_usuario',
    listar: 'listar_usuario',
  },
} as const;

export const LISTA_PERMISOS = [
  PERMISOS.roles.crear,
  PERMISOS.roles.editar,
  PERMISOS.roles.eliminar,
  PERMISOS.roles.listar,
  PERMISOS.permisos.crear,
  PERMISOS.permisos.editar,
  PERMISOS.permisos.eliminar,
  PERMISOS.permisos.listar,
  PERMISOS.usuarios.crear,
  PERMISOS.usuarios.editar,
  PERMISOS.usuarios.eliminar,
  PERMISOS.usuarios.listar,
] as const;
