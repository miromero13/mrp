export interface RouteOperationItem {
  id: string;
  machineId: string | null;
  machineName: string | null;
  operationOrder: number;
  description: string;
  setupTimeMinutes: number;
  runTimeMinutes: number;
}

export interface ProductionRouteListItem {
  id: string;
  productVersionId: string;
  productVersionName: string;
  productId: string;
  productName: string;
  name: string;
  isActive: boolean;
  operations: RouteOperationItem[];
}

export interface RouteOperationFormValue {
  machineId: string | null;
  operationOrder: number;
  description: string;
  setupTimeMinutes: number;
  runTimeMinutes: number;
}

export interface CreateProductionRouteFormValue {
  productVersionId: string;
  name: string;
  isActive: boolean;
  operations: RouteOperationFormValue[];
}
