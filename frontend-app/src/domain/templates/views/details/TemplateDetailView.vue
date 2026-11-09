<template>
  <div>
    <TemplateDetailHeader/>
    <VContainer>
      <div class="d-flex">
        <VAvatar color="secondary" icon="mdi-account" rounded="sm" size="x-large" variant="tonal"/>
        <div>
          <h1>Title</h1>
          <span>0 items</span>
        </div>
      </div>
    </VContainer>
    <h1 style="height:200vh;">Template Details</h1>
  </div>
</template>

<script lang="ts" setup>
  import TemplateDetailHeader
    from "@/domain/templates/views/details/partials/TemplateDetailHeader.vue";
</script>
