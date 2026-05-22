export enum BatchStatus {
  APPROVED = 'APPROVED',
  BLOCKED = 'BLOCKED',
  QUARANTINE = 'QUARANTINE',
}

export interface BatchListItem {
  id: string;
  batchNumber: string;
  materialId: string | null;
  materialCode: string | null;
  productId: string | null;
  productName: string | null;
  productionDate: string;
  expirationDate: string | null;
  currentStock: number;
  status: BatchStatus;
  expired: boolean;
}

export interface CreateBatchFormValue {
  batchNumber: string;
  materialId: string | null;
  productId: string | null;
  productionDate: string;
  expirationDate: string | null;
  currentStock: number;
  status: BatchStatus;
}

export interface UpdateBatchFormValue {
  expirationDate: string | null;
  status: BatchStatus;
}
