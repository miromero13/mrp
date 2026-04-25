export interface EmployeeListItem {
  internal_code: string;
  position: string;
  hire_date: Date | null;
  hourly_rate: number | null;
  status: Boolean;
  user: {
    id: string;
    name: string;
    email: string;
    phone: string
  };
}
