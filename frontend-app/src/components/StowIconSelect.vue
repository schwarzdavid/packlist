<template>
  <VInput class="stow-icon-select" color="primary" density="comfortable" details focused>
    <VField :label="label" active rounded="lg" variant="outlined">
      <VItemGroup v-model="model" class="stow-icon-select__item-group">
        <div class="stow-icon-select__icon-grid">
          <VItem v-if="!required" v-slot="{toggle, isSelected}" :value="null">
            <VBtn :active="isSelected" class="stow-icon-select__icon" color="warning"
                  icon="mdi-selection-off" variant="text" @click="toggle"/>
          </VItem>
          <VItem v-for="icon in AVAILABLE_ICONS" :key="icon" v-slot="{toggle, isSelected}"
                 :value="icon">
            <VBtn :active="isSelected" :icon="icon" active-color="primary"
                  class="stow-icon-select__icon" variant="text" @click="toggle"/>
          </VItem>
        </div>
      </VItemGroup>
    </VField>
  </VInput>
</template>

<script generic="Required extends boolean = false" lang="ts" setup>
  const AVAILABLE_ICONS = [
    'mdi-palm-tree',
    'mdi-image-filter-hdr',
    'mdi-ski',
    'mdi-tools',
    'mdi-camera-party-mode',
    'mdi-toothbrush',
    'mdi-power-plug-battery-outline',
    'mdi-snowflake',
    'mdi-weather-sunny',
    'mdi-tsunami',
    'mdi-weather-lightning-rainy',
    'mdi-wallet-travel',
    'mdi-car-convertible',
    'mdi-bus-side',
    'mdi-airplane',
    'mdi-pill',
    'mdi-party-popper',
    'mdi-human-baby-changing-table',
  ] as const

  defineProps<{
    label?: string
    required?: Required
  }>()

  const model = defineModel<Required extends true ? string : string | null | undefined>()
</script>

<style lang="scss" scoped>
@use "vuetify/settings";

.stow-icon-select {
  &__icon-grid {
    padding-block-start: settings.$field-input-padding-top;
    padding-block-end: settings.$field-input-padding-bottom;
    padding-inline-start: settings.$field-input-padding-top;
    padding-inline-end: settings.$field-input-padding-top;
    gap: calc(#{settings.$field-input-padding-top} / 2);
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(settings.$button-height, 1fr));
    max-height: calc(#{settings.$button-height * 2.2} + #{settings.$field-input-padding-top} * 2 + settings.$field-input-padding-bottom);
    overflow-y: auto;
  }

  &__icon {
    aspect-ratio: 1;
    height: unset;
    width: 100%;
  }

  &__item-group {
    position: relative;
    flex-grow: 1;
    z-index: 0;

    &::before,
    &::after {
      position: absolute;
      content: '';
      left: settings.$field-input-padding-top;
      right: settings.$field-input-padding-top;
      --shade-color: rgb(var(--v-theme-surface));
      z-index: 1;
    }

    &::before {
      height: settings.$field-input-padding-top;
      background: linear-gradient(to bottom, var(--shade-color), transparent);
    }

    &::after {
      height: settings.$field-input-padding-bottom;
      bottom: 0;
      background: linear-gradient(to top, var(--shade-color), transparent);
    }
  }
}
</style>
