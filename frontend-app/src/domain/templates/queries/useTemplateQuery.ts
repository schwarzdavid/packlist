import {defineQueryOptions} from "@pinia/colada";
import {useApi} from "@/composables/useApi.ts";
import {TemplateApi} from "@packlist/lists-api";
import {type MaybeRefOrGetter, toValue} from "vue";
import {toResponseData} from "@/utils/toResponseData.ts";

export function useTemplateQuery(templateId: MaybeRefOrGetter<number>) {
  const templateApi = useApi(TemplateApi)

  return defineQueryOptions({
    key: ['template', templateId],
    query: () => templateApi.getTemplate({
      path: {
        templateId: toValue(templateId)
      }
    }).then(toResponseData)
  })
}
