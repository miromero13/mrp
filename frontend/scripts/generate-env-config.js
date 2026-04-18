#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

// Leer el archivo .env
const envPath = path.join(__dirname, '..', '.env');
const envContent = fs.existsSync(envPath)
  ? fs.readFileSync(envPath, 'utf-8')
  : '';

// Parsear las variables de .env manualmente
const env = {};
envContent.split('\n').forEach((line) => {
  const trimmed = line.trim();
  if (trimmed && !trimmed.startsWith('#')) {
    const [key, ...valueParts] = trimmed.split('=');
    const value = valueParts.join('=').trim();
    env[key.trim()] = value.replace(/^['"]|['"]$/g, '');
  }
});

// Generar código TypeScript con las variables de entorno
const configCode = `
// AUTOGENERADO: Este archivo es generado automáticamente a partir de .env
// NO editar manualmente

export const ENV_CONFIG = {
  NG_APP_API_URL: '${env.NG_APP_API_URL || 'http://localhost:8090/api/'}',
  NG_APP_NAME: '${env.NG_APP_NAME || 'Prisma'}',
};
`;

// Escribir el archivo de configuración
const outputPath = path.join(__dirname, '..', 'src', 'app', 'core', 'config', 'env-config.ts');
fs.mkdirSync(path.dirname(outputPath), { recursive: true });
fs.writeFileSync(outputPath, configCode, 'utf-8');

console.log('✓ Archivo de configuración de entorno generado:', outputPath);

