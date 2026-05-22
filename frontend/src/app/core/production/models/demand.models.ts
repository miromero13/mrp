export enum DemandType {
  SALE_ORDER = 'SALE_ORDER',
  FORECAST = 'FORECAST',
}

export enum DemandStatus {
  PENDING = 'PENDING',
  FULFILLED = 'FULFILLED',
  CANCELLED = 'CANCELLED',
}

export interface DemandListItem {
  id: string;
  enterpriseId: string;
  enterpriseName: string;
  productId: string;
  productName: string;
  type: DemandType;
  quantity: number;
  dueDate: string;
  status: DemandStatus;
}

export interface DemandFormValue {
  productId: string;
  type: DemandType;
  quantity: number;
  dueDate: string;
  status: DemandStatus;
}
