export interface MaterialListItem {
  id: string;
  code: string;
  unitOfMeasure: string;
  stockMin: number;
  stockCurrent: number;
  unitPrice: number;
  enterprise: {
    id: string;
    name: string;
  } | null;
}

export enum MaterialMovementType {
  ENTRY = 'ENTRY',
  EXIT = 'EXIT',
}

export interface CreateMaterialFormValue {
  code: string;
  unitOfMeasure: string;
  stockMin: number;
  stockCurrent: number;
  unitPrice: number;
}

export interface CreateMaterialMovementFormValue {
  type: MaterialMovementType;
  quantity: number;
  unitPrice: number;
  reason: string;
}

export interface MaterialMovementDetailItem {
  id: string;
  quantity: number;
  unitPrice: number;
  batch: {
    id: string;
    batchNumber: string;
    currentStock: number;
    status: 'APPROVED' | 'BLOCKED' | 'QUARANTINE';
    expirationDate: string | null;
  } | null;
}

export interface MaterialMovementListItem {
  id: string;
  type: MaterialMovementType;
  reason: string;
  material: MaterialListItem;
  details: MaterialMovementDetailItem[];
  createdAt: string;
  updatedAt: string;
}
