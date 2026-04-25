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
  adminName: string;
  adminEmail: string;
  adminPassword: string;
  adminPhone: string | null;
  adminAddress: string | null;
  adminGender: 'masculino' | 'femenino' | 'otro' | null;
}
