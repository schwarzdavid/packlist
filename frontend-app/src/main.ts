import {createApp} from 'vue'
import App from './App.vue'
import {router} from './plugins/router.ts'
import {vuetify} from "@/plugins/vuetify.ts";
import {i18n} from "@/plugins/i18n.ts";
import {createPinia} from "pinia";
import {PiniaColada} from "@pinia/colada";
import {configureZod} from "@/config/configureZod.ts";

const app = createApp(App)

app.use(router)
app.use(vuetify)
app.use(i18n)
app.use(createPinia())
app.use(PiniaColada)

configureZod(i18n.global.t)

app.mount('#app')
