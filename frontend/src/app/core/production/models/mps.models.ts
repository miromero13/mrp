export enum MpsPeriodMode {
  WEEKLY = 'WEEKLY',
  MONTHLY = 'MONTHLY',
}

export interface MpsRecordListItem {
  id: string;
  enterpriseId: string;
  enterpriseName: string;
  productId: string;
  productName: string;
  periodMode: MpsPeriodMode;
  periodStartDate: string;
  forecastDemand: number;
  allocatedDemand: number;
  plannedProduction: number;
  projectedAvailableBalance: number;
}

export interface GenerateMpsFormValue {
  enterpriseId: string;
  productId: string;
  periodMode: MpsPeriodMode;
  periodCount: number;
}

export interface UpdateMpsRecordFormValue {
  plannedProduction: number;
}
