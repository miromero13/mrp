export interface WorkCenter {
  id: string;
  code: string;
  name: string;
  description?: string;
  plant: string;
  productionLine?: string;
  resourceType: 'MACHINE' | 'LABOR' | 'EQUIPMENT' | 'LINE';
  capacity: number;
  costPerHour: number;
  targetEfficiency: number;
  currentOee?: number;
  isBottleneck: boolean;
  isCriticalResource: boolean;
  status: 'ACTIVE' | 'INACTIVE' | 'MAINTENANCE' | 'SCHEDULED';
  calendar?: string;
  lastMaintenanceDate?: Date;
  nextMaintenanceDate?: Date;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface CreateWorkCenterRequest {
  code: string;
  name: string;
  description?: string;
  plant: string;
  productionLine?: string;
  resourceType: 'MACHINE' | 'LABOR' | 'EQUIPMENT' | 'LINE';
  capacity: number;
  costPerHour: number;
  targetEfficiency: number;
  isBottleneck?: boolean;
  isCriticalResource?: boolean;
  calendar?: string;
}

export interface UpdateWorkCenterRequest {
  name?: string;
  description?: string;
  productionLine?: string;
  resourceType?: 'MACHINE' | 'LABOR' | 'EQUIPMENT' | 'LINE';
  capacity?: number;
  costPerHour?: number;
  targetEfficiency?: number;
  currentOee?: number;
  isBottleneck?: boolean;
  isCriticalResource?: boolean;
  status?: 'ACTIVE' | 'INACTIVE' | 'MAINTENANCE' | 'SCHEDULED';
  calendar?: string;
  nextMaintenanceDate?: Date;
}
