<template>
  <div>
    <h1>Create Template</h1>
    <VBtn @click="createTemplate()">Create Template</VBtn>
  </div>
</template>

<script lang="ts" setup>
  import {useApi} from "@/composables/useApi.ts";
  import {TemplateApi} from "@packlist/lists-api";

  const api = useApi(TemplateApi)

  function createTemplate() {
    api.createTemplate({
      body: {
        name: 'Test Template'
      }
    })
  }
</script>
