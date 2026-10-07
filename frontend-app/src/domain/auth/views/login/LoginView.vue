<template>
  <VCardItem>
    <h1>{{ t('auth.login.title') }}</h1>
    <p class="text-medium-emphasis mb-6">{{ t('auth.login.description') }}</p>

    <VExpandTransition>
      <div v-if="isGeneralError" data-testid="error-banner">
        <VAlert :text="t('error.general')" class="mb-6" type="error"/>
      </div>
    </VExpandTransition>

    <form class="d-flex flex-column gr-2" data-testid="form" @submit.prevent="onSubmit">
      <VDefaultsProvider :defaults="{global: {disabled: isSubmitting}}">
        <VTextField v-model="identifier" :error-messages="errors.identifier"
                    :label="t('field.identifier')" autocomplete="username"
                    data-testid="identifier" prepend-inner-icon="mdi-account-outline"/>
        <VTextField v-model="password" :error-messages="errors.password"
                    :label="t('field.password')"
                    :type="passwordType" autocomplete="current-password"
                    data-testid="password" prepend-inner-icon="mdi-lock-outline">
          <template #append-inner>
            <VIconBtn :icon="passwordIcon" @click="showPassword = !showPassword"/>
          </template>
        </VTextField>
      </VDefaultsProvider>
      <VBtn :loading="isSubmitting" :text="t('cta.login')" block color="primary" size="large"
            data-testid="submit" type="submit"/>
    </form>

    <VDivider class="my-6"/>

    <I18nT class="text-medium-emphasis text-body-medium text-center"
           keypath="auth.login.registration-hint"
           scope="global" tag="p">
      <template #register>
        <RouterLink :to="{name: AuthRoutes.REGISTER}" class="text-primary font-weight-bold">
          {{ t('auth.login.register-now') }}
        </RouterLink>
      </template>
    </I18nT>
  </VCardItem>
</template>

<script lang="ts" setup>
  import {useI18n} from "vue-i18n";
  import {AuthRoutes} from "@/domain/auth/router/AuthRoutes.ts";
  import {useForm} from "vee-validate";
  import {toTypedSchema} from "@vee-validate/zod";
  import {zCredentialsDto} from '@packlist/users-api'
  import {
    useCredentialsLoginMutation
  } from "@/domain/auth/mutations/useCredentialsLoginMutation.ts";
  import {computed, ref} from "vue";
  import {useRouter} from "vue-router";
  import {ListRoutes} from "@/domain/lists/router/ListRoutes.ts";

  const {t} = useI18n()
  const {mutateAsync: loginWithCredentials} = useCredentialsLoginMutation()
  const {defineField, errors, handleSubmit, isSubmitting} = useForm({
    validationSchema: toTypedSchema(zCredentialsDto)
  })
  const router = useRouter()

  const isGeneralError = ref(false)
  const showPassword = ref(false)
  const passwordType = computed(() => showPassword.value ? 'text' : 'password')
  const passwordIcon = computed(() => showPassword.value ? 'mdi-eye-outline' : 'mdi-eye-off-outline')

  const [identifier] = defineField('identifier')
  const [password] = defineField('password')

  const onSubmit = handleSubmit(async credentialsDto => {
    try {
      await loginWithCredentials(credentialsDto)
      void router.push({name: ListRoutes.LIST})
    } catch (err) {
      // TODO: handle invalid credentials
      console.error(err)
      isGeneralError.value = true
    }
  })
</script>
