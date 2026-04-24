// models/movement.model.ts
export type MovementType = 'ENTRADA' | 'SALIDA';

export interface CreateMovementDto {
  movementType: MovementType;
  amount: number;
}