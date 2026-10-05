<template>
  <VMain class="d-flex align-center justify-center">
    <VCard class="mx-4" max-width="450" width="100%">
      <VCardTitle class="d-flex align-center justify-start gc-3 pt-4">
        <VImg v-if="current.dark" class="flex-0-0" src="@/assets/logo-dark.svg" width="40"/>
        <VImg v-else class="flex-0-0" src="@/assets/logo.svg" width="32"/>
        <span class="font-weight-semibold">Stow</span>
      </VCardTitle>
      <RouterView/>
    </VCard>
  </VMain>
</template>

<script lang="ts" setup>
  import {useTheme} from "vuetify/framework";

  const {current} = useTheme()
</script>
