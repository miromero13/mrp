export interface ProductVersionItem {
  id: string;
  name: string;
}

export interface ProductMaterialItem {
  id: string;
  code: string;
  unitOfMeasure: string;
}

export interface ProductListItem {
  id: string;
  name: string;
  description: string | null;
  productionCost: number;
  salePrice: number;
  materials: ProductMaterialItem[];
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
  materialIds: string[];
  versions: Array<{ name: string }>;
}
