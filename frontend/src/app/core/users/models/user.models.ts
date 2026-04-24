export interface UserListItem {
  id: string;
  name: string;
  email: string;
  phone: string | null;
  gender: string | null;
  address: string | null;
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
}