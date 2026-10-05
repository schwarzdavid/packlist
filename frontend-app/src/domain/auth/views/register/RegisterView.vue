<template>
  <VCardItem>
    <h1>{{ t('auth.register.title') }}</h1>
    <p class="text-medium-emphasis mb-6">{{ t('auth.register.description') }}</p>

    <form class="d-flex flex-column gr-2" @submit.prevent="onSubmit">
      <VTextField v-model="username" :error-messages="errors.username" :label="t('field.username')"
                  autocomplete="username" prepend-inner-icon="mdi-account-outline"/>
      <VTextField v-model="email" :error-messages="errors.email" :label="t('field.email')"
                  autocomplete="email" prepend-inner-icon="mdi-at"/>
      <VTextField v-model="password" :error-messages="errors.password" :label="t('field.password')"
                  autocomplete="new-password" prepend-inner-icon="mdi-lock-outline"
                  type="password"/>
      <VTextField v-model="passwordRepeat" :error-messages="errors.passwordRepeat"
                  :label="t('field.password-repeat')" autocomplete="new-password"
                  prepend-inner-icon="mdi-lock-outline" type="password"/>
      <VBtn :text="t('cta.register')" block color="primary" size="large" type="submit"/>
    </form>

    <VDivider class="my-6"/>

    <I18nT class="text-medium-emphasis text-body-medium text-center" keypath="auth.register.login-hint"
           tag="p">
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
  import {zRegistrationDto} from "@packlist/users-api";
  import {z} from "zod";
  import {AuthRoutes} from "@/domain/auth/router/AuthRoutes.ts";

  const zRegistrationForm = zRegistrationDto.extend({
    passwordRepeat: z.string()
  }).refine(values => values.password === values.passwordRepeat, {
    path: ['passwordRepeat'],
    error: () => t('validation.passwordsDoNotMatch')
  })

  const {t} = useI18n()
  const {defineField, handleSubmit, errors} = useForm({
    validationSchema: toTypedSchema(zRegistrationForm)
  })

  const [username] = defineField('username')
  const [email] = defineField('email')
  const [password] = defineField('password')
  const [passwordRepeat] = defineField('passwordRepeat')

  const onSubmit = handleSubmit(async values => {
  })
</script>
