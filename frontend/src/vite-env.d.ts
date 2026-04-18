/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly NG_APP_API_URL: string;
  readonly NG_APP_NAME: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
