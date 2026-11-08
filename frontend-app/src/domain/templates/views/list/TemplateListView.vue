<template>
  <VFadeTransition mode="out-in">
    <VProgressCircular v-if="isLoading" indeterminate/>
    <VContainer v-else-if="data">
      <TemplatePreviewList :items="data"/>
    </VContainer>
    <StowEmptyState v-else :text="t('templates.list.empty.description')"
                    :title="t('templates.list.empty.title')" icon="mdi-tag-plus-outline">
      <VBtn :text="t('templates.cta.create')" color="primary" @click="createTemplate"/>
    </StowEmptyState>
  </VFadeTransition>
</template>

<script lang="ts" setup>
  import StowEmptyState from "@/components/StowEmptyState.vue";
  import {useI18n} from "vue-i18n";
  import {useDialog} from "@schwarzdavid/ui-kit";
  import TemplateForm from "@/domain/templates/views/list/partials/TemplateCreateForm.vue";
  import type {TemplateCreateDto, TemplateDto} from "@packlist/lists-api";
  import {
    useTemplateCreateMutation
  } from "@/domain/templates/mutations/useTemplateCreateMutation.ts";
  import {useRouter} from "vue-router";
  import {TemplateRoutes} from "@/domain/templates/routes/TemplateRoutes.ts";
  import {useQuery} from "@pinia/colada";
  import {useTemplatesQuery} from "@/domain/templates/queries/useTemplatesQuery.ts";
  import TemplatePreviewList from "@/domain/templates/views/list/partials/TemplatePreviewList.vue";

  const {t} = useI18n()
  const dialog = useDialog()
  const {mutateAsync} = useTemplateCreateMutation()
  const router = useRouter()
  const {isLoading, data} = useQuery(useTemplatesQuery())

  async function createTemplate() {
    const result = await dialog.form<TemplateCreateDto, TemplateDto>({
      title: t('templates.cta.create'),
      component: TemplateForm,
      initialValue: {
        name: ''
      } satisfies TemplateCreateDto,
      action: (value) => mutateAsync(value)
    })

    if (!result) {
      return
    }

    router.push({name: TemplateRoutes.DETAILS, params: {templateId: result.templateId}})
  }
</script>
