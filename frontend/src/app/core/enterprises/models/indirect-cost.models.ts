export interface IndirectCostListItem {
  id: string;
  name: string;
  description: string | null;
  amount: number;
  enterprise: {
    id: string;
    name: string;
    nit: string;
  } | null;
}

export interface IndirectCostFormValue {
  name: string;
  description: string | null;
  amount: number;
}
