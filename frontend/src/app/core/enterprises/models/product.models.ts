export interface ProductVersionItem {
  id: string;
  name: string;
  approved: boolean;
  usedInProduction: boolean;
}

export interface ProductListItem {
  id: string;
  name: string;
  description: string | null;
  productionCost: number;
  salePrice: number;
  versions: ProductVersionItem[];
  enterprise: {
    id: string;
    name: string;
  } | null;
}

export interface CreateProductFormValue {
  name: string;
  description: string | null;
  productionCost: number;
  salePrice: number;
  versions: Array<{ name: string }>;
}
