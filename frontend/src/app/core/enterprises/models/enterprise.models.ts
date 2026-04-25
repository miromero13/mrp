export interface EnterpriseListItem {
  id: string;
  name: string;
  nit: string;
  address: string | null;
}

export interface CreateEnterpriseFormValue {
  name: string;
  nit: string;
  address: string | null;
}

