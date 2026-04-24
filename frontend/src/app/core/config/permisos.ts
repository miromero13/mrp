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
    crear: 'crear_enterprise',
    editar: 'editar_enterprise',
    eliminar: 'eliminar_enterprise',
    listar: 'listar_enterprise',
  },
  employees: {
    crear: 'crear_employee',
    editar: 'editar_employee',
    eliminar: 'eliminar_employee',
    listar: 'listar_employee',
  },
    workshifts: {
    crear: 'crear_workshift',
    editar: 'editar_workshift',
    eliminar: 'eliminar_workshift',
    listar: 'listar_workshift',
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

  PERMISOS.employees.crear,
  PERMISOS.employees.editar,
  PERMISOS.employees.eliminar,
  PERMISOS.employees.listar,

  PERMISOS.workshifts.crear,
  PERMISOS.workshifts.editar,
  PERMISOS.workshifts.eliminar,
  PERMISOS.workshifts.listar,
] as const;
