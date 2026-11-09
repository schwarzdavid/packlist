import {createApp} from 'vue'
import App from './App.vue'
import {router} from './plugins/router.ts'
import {vuetify} from "@/plugins/vuetify.ts";
import {i18n} from "@/plugins/i18n.ts";
import {createPinia} from "pinia";
import {PiniaColada} from "@pinia/colada";
import {configureZod} from "@/config/configureZod.ts";
import {uiKit} from "@/plugins/uiKit.ts";

import '@schwarzdavid/ui-kit/styles'
import '@/styles/main.scss'

const app = createApp(App)
const pinia = createPinia();

app.use(router)
app.use(vuetify)
app.use(i18n)
app.use(pinia)
app.use(PiniaColada, {pinia})
app.use(uiKit)

configureZod(i18n.global.t)

app.mount('#app')
