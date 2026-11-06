import {defineQueryOptions} from "@pinia/colada";
import {useApi} from "@/composables/useApi.ts";
import {TemplateApi} from "@packlist/lists-api";
import {toResponseData} from "@/utils/toResponseData.ts";

export function useTemplatesQuery() {
  const templateApi = useApi(TemplateApi)

  return defineQueryOptions({
    key: ['templates'],
    query: () => templateApi.getTemplates().then(toResponseData)
  })
}
