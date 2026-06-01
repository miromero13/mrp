export interface Batch {
  id: string;
  batchNumber: string;
  productionDate: string;
  expirationDate: string;
  currentStock: number;
  materialId?: string;
  productId?: string;
  enterpriseId?: string;
  status?: string;
}

export interface CreateBatchRequest {
  productionDate: string;
  expirationDate: string;
  currentStock: number;
  materialId?: string;
  productId?: string;
  enterpriseId?: string;
  status?: string;
}
