export const PERMISOS = {
  usuarios: {
    crear: 'crear_usuario',
    editar: 'editar_usuario',
    eliminar: 'eliminar_usuario',
    listar: 'listar_usuario',
  },
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
  material: {
    crear: 'crear_material',
    editar: 'editar_material',
    eliminar: 'eliminar_material',
    listar: 'listar_material',
  },
  enterprises: {
    crear: 'crear_empresa',
    editar: 'editar_empresa',
    eliminar: 'eliminar_empresa',
    listar: 'listar_empresa',
  },
  workshifts: {
    crear: 'crear_turno',
    editar: 'editar_turno',
    eliminar: 'eliminar_turno',
    listar: 'listar_turno',
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

  PERMISOS.enterprises.crear,
  PERMISOS.enterprises.editar,
  PERMISOS.enterprises.eliminar,
  PERMISOS.enterprises.listar,

  PERMISOS.workshifts.crear,
  PERMISOS.workshifts.editar,
  PERMISOS.workshifts.eliminar,
  PERMISOS.workshifts.listar,
] as const;
