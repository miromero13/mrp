export interface RoleListItem {
  id: string;
  name: string;
  permissions?: Array<{
    id: string;
    name: string;
    description: string;
  }>;
}

export interface RoleFormValue {
  name: string;
  permissionIds: string[];
}