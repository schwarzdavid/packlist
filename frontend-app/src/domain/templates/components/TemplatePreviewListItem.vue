<template>
  <VCard :to="{name: TemplateRoutes.DETAILS, params: {templateId: item.templateId}}">
    <template #prepend>
      <VAvatar :icon="item.icon || 'mdi-tag-outline'" color="secondary" rounded="sm"
               variant="tonal"/>
    </template>
    <template #title>
      <span class="text-body-large font-weight-bold">{{ item.name }}</span>
    </template>
    <template #subtitle>
      <span>{{ t('templates.preview.count', {count: item.itemsCount}) }}</span>
    </template>
    <template #append>
      <VIcon icon="mdi-chevron-right"/>
    </template>
  </VCard>
</template>

<script lang="ts" setup>
  import type {TemplatePreviewDto} from "@packlist/lists-api";
  import {TemplateRoutes} from "@/domain/templates/routes/TemplateRoutes.ts";
  import {useI18n} from "vue-i18n";

  defineProps<{
    item: TemplatePreviewDto
  }>()

  const {t} = useI18n()
</script>
