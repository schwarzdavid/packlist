<template>
  <VVirtualScroll v-slot="{item}" :items="items" item-key="templateId" renderless>
    <TemplatePreviewListItem :item="item"/>
  </VVirtualScroll>
</template>

<script lang="ts" setup>
  import type {TemplatePreviewDto} from "@packlist/lists-api";
  import TemplatePreviewListItem from "@/domain/templates/components/TemplatePreviewListItem.vue";

  defineProps<{
    items: TemplatePreviewDto[]
  }>()
</script>
