<template>
  <div>
    <h1>Template</h1>
    <VBtn :to="{name: TemplateRoutes.CREATE}">Create template</VBtn>
  </div>
</template>

<script lang="ts" setup>
  import {TemplateRoutes} from "@/domain/templates/routes/TemplateRoutes.ts";
</script>
