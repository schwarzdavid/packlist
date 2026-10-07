import {test as testBase} from 'vitest'
import {createPinia, disposePinia, type Pinia} from "pinia";
import {type App, createApp} from "vue";
import {PiniaColada} from "@pinia/colada";

interface ColadaExtension {
  app: App,
  pinia: Pinia
}

export const it = testBase.extend<ColadaExtension>({
  async pinia({}, use) {
    const pinia = createPinia()
    await use(pinia).finally(() => disposePinia(pinia))
  },
  async app({pinia}, use) {
    const app = createApp({})

    app.use(pinia)
    app.use(PiniaColada, {pinia})

    await use(app)
  }
})
