<template>
  <RouterView v-slot="{Component}" name="header">
    <VFadeTransition mode="out-in">
      <component :is="Component"/>
    </VFadeTransition>
  </RouterView>
  <VMain>
    <RouterView v-slot="{Component}">
      <VScrollXTransition mode="out-in">
        <component :is="Component"/>
      </VScrollXTransition>
    </RouterView>
  </VMain>
  <VBottomNavigation border="t">
    <VBtn :text="t('navigation.lists')" :to="{name: ListRoutes.LIST}"
          prepend-icon="mdi-playlist-check"/>
    <VBtn :text="t('navigation.templates')" :to="{name: TemplateRoutes.LIST}"
          prepend-icon="mdi-tag-multiple-outline"/>
    <VBtn :text="t('navigation.account')" :to="{name: AccountRoutes.ACCOUNT}"
          prepend-icon="mdi-account-circle-outline"/>
  </VBottomNavigation>
</template>

<script lang="ts" setup>
  import {useI18n} from "vue-i18n";
  import {ListRoutes} from "@/domain/lists/router/ListRoutes.ts";
  import {TemplateRoutes} from "@/domain/templates/routes/TemplateRoutes.ts";
  import {AccountRoutes} from "@/domain/account/router/AccountRoutes.ts";

  const {t} = useI18n()
</script>
