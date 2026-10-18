<template>
  <VEmptyState>
    <template v-if="icon" #media>
      <VAvatar :color="color || 'primary'" class="mb-6" size="128" variant="tonal">
        <VIcon :icon="icon" size="72"/>
      </VAvatar>
    </template>
    <template v-if="headline" #headline>
      <span class="text-label-large">{{ headline }}</span>
    </template>
    <template v-if="title" #title>
      <h1 class="my-0 text-headline-large">{{ title }}</h1>
    </template>
    <template v-if="text" #text>
      <p class="text-body-large text-medium-emphasis my-1">{{ text }}</p>
    </template>
    <template v-if="(target && targetTitle) || showBack" #actions>
      <div class="d-flex flex-column align-center gr-3">
        <VBtn v-if="target" :text="targetTitle" :to="target" color="primary"/>
        <VBtn v-if="showBack" :text="t('cta.go-back')" color="primary" variant="text"
              @click="router.back()"/>
      </div>
    </template>
  </VEmptyState>
</template>

<script lang="ts" setup>
  import {type RouteLocationRaw, useRouter} from "vue-router";
  import {useI18n} from "vue-i18n";

  defineProps<{
    headline?: string,
    title?: string,
    text?: string,
    icon?: string,
    color?: string,
    target?: RouteLocationRaw,
    targetTitle?: string,
    showBack?: boolean
  }>()

  const {t} = useI18n();
  const router = useRouter()
</script>
