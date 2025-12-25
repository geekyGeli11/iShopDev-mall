/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly NODE_ENV: 'development' | 'production' | 'staging'
  readonly MODE: string
  readonly VITE_API_BASE_URL: string
  readonly VITE_MALL_ADMIN_API: string
  readonly VITE_MALL_PORTAL_API: string
  readonly VITE_MALL_SEARCH_API: string
  readonly VITE_APP_TITLE: string
  readonly VITE_APP_VERSION: string
  readonly VITE_DEBUG_MODE: 'true' | 'false'
  readonly VITE_MOCK_PAYMENT: 'true' | 'false'
  readonly VITE_LOG_LEVEL: 'debug' | 'info' | 'warn' | 'error'
}

interface ImportMeta {
  readonly env: ImportMetaEnv
} 