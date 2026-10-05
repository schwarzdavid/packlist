import {createApp} from 'vue'
import App from './App.vue'
import {router} from './plugins/router.ts'
import {vuetify} from "@/plugins/vuetify.ts";
import {i18n} from "@/plugins/i18n.ts";
import {createPinia} from "pinia";
import {PiniaColada} from "@pinia/colada";
import {z} from "zod";

const app = createApp(App)

app.use(router)
app.use(vuetify)
app.use(i18n)
app.use(createPinia())
app.use(PiniaColada)

z.config({
  customError(issue) {
    const t = i18n.global.t

    if (issue.code === 'invalid_type' || (issue.code === 'too_small' && issue.minimum === 1)) {
      return t('validation.required')
    }
    if (issue.code === 'too_small') {
      return t('validation.minLength', {min: issue.minimum})
    }
    if (issue.code === 'too_big') {
      return t('validation.maxLength', {max: issue.maximum})
    }
    if (issue.code === 'invalid_format') {
      switch (issue.format) {
        case 'email':
          return t('validation.email')
        default:
          if (import.meta.env.DEV) {
            console.warn('Unmapped format error', issue)
          }
      }
    } else if (import.meta.env.DEV) {
      console.warn('Unmapped error', issue)
    }

    return t('validation.invalid')
  }
})

app.mount('#app')
