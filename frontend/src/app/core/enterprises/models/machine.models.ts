export interface MachineListItem {
  id: string;
  name: string;
  description: string | null;
  cost: number;
  enterprise: {
    id: string;
    name: string;
    nit: string;
  } | null;
}

export interface MachineFormValue {
  name: string;
  description: string | null;
  cost: number;
}
