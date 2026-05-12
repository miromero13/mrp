export interface WorkShiftListItem {
  id: string;
  name: string;
  startdate: Date;
  enddate: Date;
  enterprise: {
    id: string;
  } | null;
}

export interface CreateWorkShiftFormValue {
  name: string;
  startdate: Date;
  enddate: Date;

}

