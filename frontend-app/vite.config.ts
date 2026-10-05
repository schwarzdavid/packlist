import {fileURLToPath, URL} from 'node:url'
import {dirname, resolve} from 'node:path'
import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import vuetify, {transformAssetUrls} from "vite-plugin-vuetify";
import i18n from '@intlify/unplugin-vue-i18n/vite'
import webfontDownload from "vite-plugin-webfont-dl";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue({
      template: {
        transformAssetUrls
      }
    }),
    vueDevTools(),
    vuetify(),
    i18n({
      include: resolve(dirname(fileURLToPath(import.meta.url)), 'src/locales/**')
    }),
    webfontDownload()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
