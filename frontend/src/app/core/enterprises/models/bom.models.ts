export interface BomItemListItem {
  id: string;
  productVersionId: string;
  productVersionName: string;
  approved: boolean;
  usedInProduction: boolean;
  materialId: string;
  materialCode: string;
  materialUnitOfMeasure: string;
  materialUnitPrice: number;
  quantity: number;
  scrapFactor: number;
  estimatedCost: number;
}

export interface CreateBomItemFormValue {
  productVersionId: string;
  materialId: string;
  quantity: number;
  scrapFactor: number;
}
