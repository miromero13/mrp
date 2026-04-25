export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthUser {
  id: string;
  name: string;
  email: string;
  phone: string | null;
  gender: string | null;
  address: string | null;
  enterprise: {
    id: string;
    name: string;
    nit: string;
    address: string | null;
  } | null;
  role: {
    id: string;
    name: string;
    permissions: Array<{
      id: string;
      name: string;
      description: string;
    }>;
  } | null;
}

export interface LoginResponseData {
  access_token: string;
  user: AuthUser;
}

export interface ApiResponse<T> {
  statusCode: number;
  message: string;
  error: string | null;
  data: T | null;
  countData: number | null;
}
