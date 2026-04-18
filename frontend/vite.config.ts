import { defineConfig, loadEnv } from 'vite';

export default defineConfig(({ command, mode }) => {
  // Cargar variables de .env
  const env = loadEnv(mode, process.cwd(), 'NG_APP_');

  return {
    server: {
      port: 4200,
      proxy: {
        '/api': {
          target: 'http://localhost:8090',
          changeOrigin: true,
        },
      },
    },
    // Inyectar variables de entorno para que estén disponibles en import.meta.env
    define: {
      'import.meta.env.NG_APP_API_URL': JSON.stringify(
        env.NG_APP_API_URL || 'http://localhost:8090/api/'
      ),
      'import.meta.env.NG_APP_NAME': JSON.stringify(env.NG_APP_NAME || 'Prisma'),
    },
  };
});
