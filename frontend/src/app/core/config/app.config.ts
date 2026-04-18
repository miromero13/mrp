import { ENV_CONFIG } from './env-config';

const appNameFromNgEnv = ENV_CONFIG.NG_APP_NAME;

export const APP_NAME = appNameFromNgEnv?.trim() || 'Frontend';
