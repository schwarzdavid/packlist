<template>
  <StowEmptyState :text="t('templates.list.empty.description')"
                  :title="t('templates.list.empty.title')" icon="mdi-tag-plus-outline">
    <VBtn :text="t('templates.cta.create')" color="primary" @click="createTemplate"/>
  </StowEmptyState>
</template>

<script lang="ts" setup>
  import StowEmptyState from "@/components/StowEmptyState.vue";
  import {useI18n} from "vue-i18n";
  import {useDialog} from "@schwarzdavid/ui-kit";
  import TemplateForm from "@/domain/templates/views/list/partials/TemplateCreateForm.vue";
  import type {TemplateCreateDto} from "@packlist/lists-api";

  const {t} = useI18n()
  const dialog = useDialog()

  async function createTemplate() {
    const result = await dialog.form<TemplateCreateDto>({
      title: t('templates.cta.create'),
      component: TemplateForm,
      initialValue: {
        name: ''
      } satisfies TemplateCreateDto
    })

    console.log(result)
  }
</script>
