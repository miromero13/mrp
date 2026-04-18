import { ENV_CONFIG } from './env-config';

const apiUrlFromEnv = ENV_CONFIG.NG_APP_API_URL;

function normalizeApiBaseUrl(url?: string): string {
  const fallback = 'http://localhost:8090/api/';
  const value = (url ?? fallback).trim();
  return value.endsWith('/') ? value : `${value}/`;
}

export const API_BASE_URL = normalizeApiBaseUrl(apiUrlFromEnv);

export function apiEndpoint(path: string): string {
  const cleanPath = path.startsWith('/') ? path.slice(1) : path;
  return `${API_BASE_URL}${cleanPath}`;
}
