<template>
  <VMain>
    <StowEmptyState :color="avatarColor" :headline="t('error.page.headline', {status})" :icon="avatarIcon"
                    :target="{name: ListRoutes.LIST}"
                    :target-title="t('cta.go-home')" :text="t(`error.page.${status}.description`)"
                    :title="t(`error.page.${status}.title`)" show-back/>
  </VMain>
</template>

<script lang="ts" setup>
  import {useI18n} from "vue-i18n";
  import {ListRoutes} from "@/domain/lists/router/ListRoutes.ts";
  import {computed} from "vue";
  import StowEmptyState from "@/components/StowEmptyState.vue";

  const props = defineProps<{
    status: number
  }>()

  const {t} = useI18n()

  const avatarColor = computed(() => props.status === 404 ? 'primary' : 'error')
  const avatarIcon = computed(() => props.status === 404 ? 'mdi-image-broken' : 'mdi-alert-circle')
</script>
