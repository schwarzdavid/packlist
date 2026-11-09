<template>
  <StowQueryState :query="query">
    <template #default="{data}">
      <div>
        <TemplateDetailToolbar/>
        <VContainer>
          <TemplateDetailsHeader v-if="data" :template="data"/>
          <VCard>
            <VList v-if="data?.items.length">
              <VListItem title="Placehodler"/>
            </VList>
            <VEmptyState v-else>
              <template #media>
                <VAvatar color="primary" size="x-large" variant="tonal">
                  <VIcon icon="mdi-playlist-remove" size="x-large"/>
                </VAvatar>
              </template>
              <template #title>
                <h1 class="mb-0 mt-3 text-title-large">{{ t('templates.detail.no-items') }}</h1>
              </template>
            </VEmptyState>
            <VDivider/>
            <VCardText>
              <form class="d-flex align-center gc-4" @submit.prevent>
                <VTextField hide-details label="Add item"/>
                <VBtn color="primary" icon="mdi-plus" variant="flat"/>
              </form>
            </VCardText>
          </VCard>
        </VContainer>
        <VFab :active="!!data?.items.length" app appear color="secondary"
              density="compact" order="10" prepend-icon="mdi-playlist-plus"
              text="Create from template"></VFab>
        <h1 style="height:200vh;">Template Details</h1>
      </div>
    </template>
  </StowQueryState>
</template>

<script lang="ts" setup>
  import TemplateDetailToolbar
    from "@/domain/templates/views/details/partials/TemplateDetailToolbar.vue";
  import {useQuery} from "@pinia/colada";
  import {useTemplateQuery} from "@/domain/templates/queries/useTemplateQuery.ts";
  import {useRouteParams} from "@vueuse/router";
  import {useI18n} from "vue-i18n";
  import TemplateDetailsHeader
    from "@/domain/templates/views/details/partials/TemplateDetailsHeader.vue";
  import StowQueryState from "@/components/StowQueryState.vue";

  const templateId = useRouteParams('templateId', '', {transform: Number})
  const query = useQuery(useTemplateQuery(templateId))
  const {t} = useI18n()
</script>
