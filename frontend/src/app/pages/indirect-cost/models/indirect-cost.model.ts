export type Currency = 'BOB' | 'USD';
export type DistributionCriterion = 'AREA' | 'CONSUMO' | 'PORCENTAJE_FIJO';

export interface IndirectCost {
  id: string;
  categoryId: string;
  categoryName: string;
  amount: number;
  currency: Currency;
  startDate: string;
  endDate: string;
  distributionCriterion: DistributionCriterion;
  costCenterId: string;
  costCenterName: string;
  active: boolean;
}

export interface IndirectCostRequest {
  categoryId: string;
  amount: number;
  currency: Currency;
  startDate: string;
  endDate: string;
  distributionCriterion: DistributionCriterion;
  costCenterId: string;
}

export interface LookupItem {
  id: string;
  name: string;
}
