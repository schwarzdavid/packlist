import {zRegistrationDto} from "@packlist/users-api";
import {z} from "zod";
import type {ComposerTranslation} from "vue-i18n";

export function createRegistrationFormValidation(t: ComposerTranslation) {
  return zRegistrationDto.extend({
    passwordRepeat: z.string()
  }).refine(values => values.password === values.passwordRepeat, {
    path: ['passwordRepeat'],
    error: () => t('validation.samePassword')
  })
}
