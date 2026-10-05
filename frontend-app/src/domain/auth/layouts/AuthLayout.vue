<template>
  <VMain class="d-flex align-center justify-center">
    <VCard class="mx-4" max-width="450" width="100%">
      <VCardTitle class="d-flex align-center justify-start gc-3 pt-4">
        <VImg v-if="current.dark" class="flex-0-0" src="@/assets/logo-dark.svg" width="40"/>
        <VImg v-else class="flex-0-0" src="@/assets/logo.svg" width="32"/>
        <span class="font-weight-semibold">Stow</span>
      </VCardTitle>
      <div ref="animation-container" class="animation-container">
        <RouterView v-slot="{Component}">
          <VFadeTransition leave-absolute @before-leave="beforeLeave" @before-enter="beforeEnter"
                           @after-enter="afterEnter">
            <component :is="Component"/>
          </VFadeTransition>
        </RouterView>
      </div>
    </VCard>
  </VMain>
</template>

<script lang="ts" setup>
  import {useTheme} from "vuetify/framework";
  import {useTemplateRef} from "vue";

  const {current} = useTheme()
  const animationContainer = useTemplateRef('animation-container')

  function beforeLeave(el: HTMLElement) {
    if (!animationContainer.value) {
      return
    }
    animationContainer.value.style.height = el.getBoundingClientRect().height + 'px'
  }

  function beforeEnter(el: HTMLElement) {

    requestAnimationFrame(() => {
      if (!animationContainer.value) {
        return
      }
      animationContainer.value.style.height = el.getBoundingClientRect().height + 'px'
    })
  }

  function afterEnter() {
    if (!animationContainer.value) {
      return
    }
    animationContainer.value.style.height = ''
  }
</script>

<style lang="scss" scoped>
@use "vuetify/settings";

.animation-container {
  transition: height settings.$transition-duration-root ease;
}
</style>
