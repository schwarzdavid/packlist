<template>
  <DefineActivator v-slot="props">
    <VBtn icon="mdi-dots-vertical" v-bind="props"/>
  </DefineActivator>
  <DefineActions>
    <VList>
      <VListItem :title="t('templates.cta.delete')" append-icon="mdi-delete-outline"
                 base-color="error"/>
    </VList>
  </DefineActions>
  <VToolbar class="position-sticky top-0" style="z-index: 1">
    <template #prepend>
      <VBtn icon="mdi-chevron-left" @click="goBack()"/>
    </template>
    <template #append>
      <VBottomSheet v-if="mobile">
        <template #activator="{props}">
          <UseActivator v-bind="props"/>
        </template>
        <UseActions/>
      </VBottomSheet>
      <VMenu v-else>
        <template #activator="{props}">
          <UseActivator v-bind="props"/>
        </template>
        <UseActions/>
      </VMenu>
    </template>
  </VToolbar>
</template>

<script lang="ts" setup>
  import {useRouter} from "vue-router";
  import {TemplateRoutes} from "@/domain/templates/routes/TemplateRoutes.ts";
  import {useDisplay} from "vuetify/framework";
  import {createReusableTemplate} from "@vueuse/core";
  import {useI18n} from "vue-i18n";

  const router = useRouter()
  const {mobile} = useDisplay()
  const {t} = useI18n()
  const [DefineActivator, UseActivator] = createReusableTemplate()
  const [DefineActions, UseActions] = createReusableTemplate()

  function goBack() {
    if (window.history.state?.back) {
      return void router.back()
    }
    router.push({name: TemplateRoutes.LIST})
  }
</script>
