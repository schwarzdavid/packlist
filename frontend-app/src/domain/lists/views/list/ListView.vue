<template>
  <StowEmptyState :target="{name: ListRoutes.CREATE}" :target-title="t('lists.list.create')"
                  :text="t('lists.list.empty.description')" :title="t('lists.list.empty.title')"
                  icon="mdi-playlist-plus"/>
</template>

<script lang="ts" setup>
  import StowEmptyState from "@/components/StowEmptyState.vue";
  import {useI18n} from "vue-i18n";
  import {ListRoutes} from "@/domain/lists/router/ListRoutes.ts";

  const {t} = useI18n()
</script>
