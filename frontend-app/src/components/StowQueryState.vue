<template>
  <div class="d-flex flex-column justify-start align-stretch h-100">
    <VFadeTransition mode="out-in">
      <slot v-if="defaultSlotProps" v-bind="defaultSlotProps"/>
      <slot v-else-if="query.isPending.value" name="loading">
        <div class="stow-query-state__spinner">
          <VProgressCircular indeterminate size="64"/>
        </div>
      </slot>
      <template v-else-if="showErrorState">
        <slot v-if="isAccessDeniedError" :error="query.error.value!" name="accessDenied"/>
        <slot v-else-if="isNotFoundError" :error="query.error.value!" name="notFound"/>
        <slot v-else :error="query.error.value!" name="error"/>
      </template>
      <slot v-else name="empty">
        <StowEmptyState class="justify-center"/>
      </slot>
    </VFadeTransition>
  </div>
</template>

<script generic="TData = unknown, TError = Error, TDataInitial extends TData | undefined = undefined" lang="ts"
        setup>
  import type {UseQueryReturn} from "@pinia/colada";
  import StowEmptyState from "@/components/StowEmptyState.vue";
  import {computed, watch} from "vue";
  import {HttpStatusCode, isAxiosError} from "axios";

  interface DefaultSlotProps<TInitial extends boolean> {
    isInitial: TInitial,
    data: TInitial extends true
      ? TDataInitial extends undefined ? TData : TDataInitial
      : TData
  }

  const {query} = defineProps<{
    query: UseQueryReturn<TData, TError, TDataInitial>
  }>()

  const emit = defineEmits<{
    nonFatalError: [TError]
  }>()

  defineSlots<{
    loading(): unknown,
    accessDenied(props: { error: TError }): unknown,
    notFound(props: { error: TError }): unknown,
    error(props: { error: TError }): unknown,
    empty(): unknown,
    default<TPending extends boolean>(props: DefaultSlotProps<TPending>): unknown
  }>()

  const hasData = computed(() => Array.isArray(query.data.value) ? query.data.value.length > 0 : !!query.data.value)
  const showErrorState = computed(() => query.error.value && !hasData.value)
  const isAccessDeniedError = computed(() => isAxiosError(query.error.value) && query.error.value.status === HttpStatusCode.Forbidden)
  const isNotFoundError = computed(() => isAxiosError(query.error.value) && query.error.value.status === HttpStatusCode.NotFound)

  const defaultSlotProps = computed<DefaultSlotProps<boolean> | undefined>(() => {
    if (hasData.value) {
      return {
        isInitial: query.isPending.value,
        data: query.data.value
      } as DefaultSlotProps<boolean>
    }
    return
  })

  watch(query.error, newError => {
    if (newError && hasData.value) {
      emit('nonFatalError', newError)
    }
  })
</script>

<style lang="scss" scoped>
.stow-query-state__spinner {
  display: flex;
  width: calc(100dvw - var(--v-layout-left) - var(--v-layout-right));
  height: calc(100dvh - var(--v-layout-top) - var(--v-layout-bottom));
  align-items: center;
  justify-content: center;
}
</style>
