// models/material.model.ts
export interface Material {
  id: string; // UUID
  code: string;
  name: string;
  description?: string;
  measureUnit: string;
  currentStock: number;
  minimumStock: number;
  state: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateMaterialDto {
  code: string;
  name: string;
  description?: string;
  measureUnit: string;
  currentStock: number;
  minimumStock: number;
  state: boolean;
}

export interface UpdateMaterialDto {
  name?: string;
  description?: string;
  measureUnit?: string;
  currentStock?: number;
  minimumStock?: number;
}