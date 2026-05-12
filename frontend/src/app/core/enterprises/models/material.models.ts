export interface MaterialListItem {
  id: string;
  code: string;
  unitOfMeasure: string;
  stockMin: number;
  stockCurrent: number;
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
