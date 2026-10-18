<template>
  <VMain>
    <VEmptyState>
      <template #media>
        <VAvatar :color="avatarColor" class="mb-4" size="128" variant="tonal">
          <VIcon :icon="avatarIcon" size="72"/>
        </VAvatar>
      </template>
      <template #headline>
        <span class="text-label-large">{{ t('error.page.headline', {status}) }}</span>
      </template>
      <template #title>
        <h1 class="my-0 text-headline-large">{{ t(`error.page.${status}.title`) }}</h1>
      </template>
      <template #text>
        <p class="text-body-large text-medium-emphasis">{{
            t(`error.page.${status}.description`)
          }}</p>
      </template>
      <template #actions>
        <div class="d-flex flex-column align-center gr-3">
          <VBtn :text="t('cta.go-home')" :to="{name: ListRoutes.LIST}" color="primary"/>
          <VBtn :text="t('cta.go-back')" color="primary" variant="text" @click="router.back()"/>
        </div>
      </template>
    </VEmptyState>
  </VMain>
</template>

<script lang="ts" setup>
  import {useI18n} from "vue-i18n";
  import {ListRoutes} from "@/domain/lists/router/ListRoutes.ts";
  import {useRouter} from "vue-router";
  import {computed} from "vue";

  const props = defineProps<{
    status: number
  }>()

  const {t} = useI18n()
  const router = useRouter()

  const avatarColor = computed(() => props.status === 404 ? 'primary' : 'error')
  const avatarIcon = computed(() => props.status === 404 ? 'mdi-image-broken' : 'mdi-alert-circle')
</script>
