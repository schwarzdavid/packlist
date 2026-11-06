import type {TemplateCreateDto, TemplateDto} from "@packlist/lists-api";
import {TemplateApi} from "@packlist/lists-api";
import {useMutation, type UseMutationReturn, useQueryCache} from "@pinia/colada";
import {useApi} from "@/composables/useApi.ts";
import {toResponseData} from "@/utils/toResponseData.ts";

export function useTemplateCreateMutation(): UseMutationReturn<TemplateDto, TemplateCreateDto, Error> {
  const templateApi = useApi(TemplateApi)
  const cache = useQueryCache()

  return useMutation({
    mutation(templateCreateDto: TemplateCreateDto) {
      return templateApi.createTemplate({
        body: templateCreateDto
      }).then(toResponseData)
    },
    async onSuccess(template) {
      cache.setQueryData(['template', template.templateId], template)
      await cache.invalidateQueries({
        key: ['templates'],
      })
    }
  })
}
