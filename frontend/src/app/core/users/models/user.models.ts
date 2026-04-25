export interface UserListItem {
  id: string;
  name: string;
  email: string;
  phone: string | null;
  gender: string | null;
  address: string | null;
  enterprise: {
    id: string;
    name: string;
    nit: string;
  } | null;
  role: {
    id: string;
    name: string;
  } | null;
}

export interface CreateUserFormValue {
  name: string;
  email: string;
  password: string;
  roleId: string;
  phone: string | null;
  address: string | null;
  gender: 'masculino' | 'femenino' | 'otro' | null;
  enterpriseId?: string | null;
  workShiftAssignments: Array<{
    workShiftId: string;
    dayOfWeek: string;
  }>;
}
