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
    movimientos: {
      crear: 'crear_movimiento_material',
      listar: 'listar_movimiento_material',
    },
  },
  warehouses: {
    batches: {
      crear: 'crear_lote',
      editar: 'editar_lote',
      eliminar: 'eliminar_lote',
      listar: 'listar_lote',
    },
  },
  products: {
    crear: 'crear_producto',
    editar: 'editar_producto',
    eliminar: 'eliminar_producto',
    listar: 'listar_producto',
  },
  bom: {
    crear: 'crear_bom',
    editar: 'editar_bom',
    eliminar: 'eliminar_bom',
    listar: 'listar_bom',
  },
  production: {
    demands: {
      crear: 'crear_demanda',
      editar: 'editar_demanda',
      eliminar: 'eliminar_demanda',
      listar: 'listar_demanda',
    },
    mps: {
      gestionar: 'gestionar_mps',
    },
  },
  productionRoutes: {
    crear: 'crear_ruta_produccion',
    editar: 'editar_ruta_produccion',
    eliminar: 'eliminar_ruta_produccion',
    listar: 'listar_ruta_produccion',
  },
  enterprises: {
    crear: 'crear_empresa',
    editar: 'editar_empresa',
    eliminar: 'eliminar_empresa',
    listar: 'listar_empresa',
  },
  machines: {
    crear: 'crear_maquinaria',
    editar: 'editar_maquinaria',
    eliminar: 'eliminar_maquinaria',
    listar: 'listar_maquinaria',
  },
  indirectCosts: {
    crear: 'crear_costo_indirecto',
    editar: 'editar_costo_indirecto',
    eliminar: 'eliminar_costo_indirecto',
    listar: 'listar_costo_indirecto',
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

  PERMISOS.machines.crear,
  PERMISOS.machines.editar,
  PERMISOS.machines.eliminar,
  PERMISOS.machines.listar,

  PERMISOS.indirectCosts.crear,
  PERMISOS.indirectCosts.editar,
  PERMISOS.indirectCosts.eliminar,
  PERMISOS.indirectCosts.listar,

  PERMISOS.material.crear,
  PERMISOS.material.editar,
  PERMISOS.material.eliminar,
  PERMISOS.material.listar,
  PERMISOS.material.movimientos.crear,
  PERMISOS.material.movimientos.listar,

  PERMISOS.warehouses.batches.crear,
  PERMISOS.warehouses.batches.editar,
  PERMISOS.warehouses.batches.eliminar,
  PERMISOS.warehouses.batches.listar,

  PERMISOS.products.crear,
  PERMISOS.products.editar,
  PERMISOS.products.eliminar,
  PERMISOS.products.listar,

  PERMISOS.bom.crear,
  PERMISOS.bom.editar,
  PERMISOS.bom.eliminar,
  PERMISOS.bom.listar,

  PERMISOS.production.demands.crear,
  PERMISOS.production.demands.editar,
  PERMISOS.production.demands.eliminar,
  PERMISOS.production.demands.listar,
  PERMISOS.production.mps.gestionar,

  PERMISOS.productionRoutes.crear,
  PERMISOS.productionRoutes.editar,
  PERMISOS.productionRoutes.eliminar,
  PERMISOS.productionRoutes.listar,

  PERMISOS.workshifts.crear,
  PERMISOS.workshifts.editar,
  PERMISOS.workshifts.eliminar,
  PERMISOS.workshifts.listar,
] as const;
