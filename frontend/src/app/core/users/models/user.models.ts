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