<template>
  <VCardItem>
    <h1>{{ t('auth.register.title') }}</h1>
    <p class="text-medium-emphasis mb-6">{{ t('auth.register.description') }}</p>

    <VExpandTransition>
      <div v-if="isGeneralError" data-testid="error-banner">
        <VAlert :text="t('error.general')" class="mb-6" type="error"/>
      </div>
    </VExpandTransition>

    <form class="d-flex flex-column gr-2" data-testid="form" @submit.prevent="onSubmit">
      <VDefaultsProvider :defaults="{global: {disabled: isSubmitting}}">
        <VTextField v-model="username" :error-messages="errors.username"
                    :label="t('field.username')"
                    autocomplete="username" data-testid="username"
                    prepend-inner-icon="mdi-account-outline"/>
        <VTextField v-model="email" :error-messages="errors.email" :label="t('field.email')"
                    autocomplete="email" data-testid="email" prepend-inner-icon="mdi-at"/>
        <VTextField v-model="password" :error-messages="errors.password"
                    :label="t('field.password')"
                    :type="passwordType" autocomplete="new-password"
                    data-testid="password" prepend-inner-icon="mdi-lock-outline">
          <template #append-inner>
            <VIconBtn :icon="passwordIcon" @click="showPassword = !showPassword"/>
          </template>
        </VTextField>
        <VTextField v-model="passwordRepeat" :error-messages="errors.passwordRepeat"
                    :label="t('field.password-repeat')" :type="passwordType"
                    autocomplete="new-password" data-testid="password-repeat"
                    prepend-inner-icon="mdi-lock-outline">
          <template #append-inner>
            <VIconBtn :icon="passwordIcon" @click="showPassword = !showPassword"/>
          </template>
        </VTextField>
      </VDefaultsProvider>
      <VBtn :loading="isSubmitting" :text="t('cta.register')" block color="primary" size="large"
            data-testid="submit" type="submit"/>
    </form>

    <VDivider class="my-6"/>

    <I18nT class="text-medium-emphasis text-body-medium text-center"
           keypath="auth.register.login-hint"
           scope="global" tag="p">
      <template #login>
        <RouterLink :to="{name: AuthRoutes.LOGIN}" class="font-weight-bold text-primary">
          {{ t('auth.register.login') }}
        </RouterLink>
      </template>
    </I18nT>
  </VCardItem>
</template>

<script lang="ts" setup>
  import {useI18n} from "vue-i18n";
  import {useForm} from "vee-validate";
  import {toTypedSchema} from "@vee-validate/zod";
  import {AuthRoutes} from "@/domain/auth/router/AuthRoutes.ts";
  import {useRegistrationMutation} from "@/domain/auth/mutations/useRegistrationMutation.ts";
  import {computed, ref} from "vue";
  import {
    createRegistrationFormValidation
  } from "@/domain/auth/views/register/utils/createRegistrationFormValidation.ts";
  import {useRouter} from "vue-router";
  import {ListRoutes} from "@/domain/lists/router/ListRoutes.ts";

  const {t} = useI18n()
  const {mutateAsync: registerUser} = useRegistrationMutation()
  const {defineField, handleSubmit, errors, isSubmitting} = useForm({
    validationSchema: toTypedSchema(createRegistrationFormValidation(t))
  })
  const router = useRouter()

  const isGeneralError = ref(false)
  const showPassword = ref(false)
  const passwordType = computed(() => showPassword.value ? 'text' : 'password')
  const passwordIcon = computed(() => showPassword.value ? 'mdi-eye-outline' : 'mdi-eye-off-outline')

  const [username] = defineField('username')
  const [email] = defineField('email')
  const [password] = defineField('password')
  const [passwordRepeat] = defineField('passwordRepeat')

  const onSubmit = handleSubmit(async values => {
    try {
      await registerUser(values)
      void router.push({name: ListRoutes.LIST})
    } catch (err) {
      // TODO: Handle 409 differently
      console.error(err)
      isGeneralError.value = true
    }
  })
</script>
