<template>
  <div class="d-flex align-center gc-3 mb-3 py-6">
    <VAvatar color="secondary" rounded="sm" size="x-large" variant="tonal">
      <VIcon :icon="template.icon || 'mdi-tag-outline'"/>
    </VAvatar>
    <div>
      <h1 class="my-0 text-title-large font-weight-bold">{{ template.name }}</h1>
      <span>{{ template.items.length }} items</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import type {TemplateDto} from "@packlist/lists-api";

  defineProps<{
    template: TemplateDto
  }>()
</script>
