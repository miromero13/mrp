export enum MachineStatus {
  ACTIVE = 'ACTIVE',
  MAINTENANCE = 'MAINTENANCE',
  INACTIVE = 'INACTIVE',
}

export interface MachineListItem {
  id: string;
  serialNumber: string;
  name: string;
  description: string | null;
  cost: number;
  status: MachineStatus;
  enterprise: {
    id: string;
    name: string;
    nit: string;
  } | null;
}

export interface MachineFormValue {
  serialNumber: string;
  name: string;
  description: string | null;
  cost: number;
  status: MachineStatus;
}
