import {z} from "zod";
import {type ComposerTranslation} from "vue-i18n";

export function configureZod(t: ComposerTranslation) {
  z.config({
    customError(issue) {
      if (issue.code === 'invalid_type' || (issue.code === 'too_small' && issue.minimum === 1)) {
        return t('validation.required')
      }
      if (issue.code === 'too_small') {
        return t('validation.minLength', {min: issue.minimum})
      }
      if (issue.code === 'too_big') {
        return t('validation.maxLength', {max: issue.maximum})
      }
      if (issue.code === 'invalid_format') {
        switch (issue.format) {
          case 'email':
            return t('validation.email')
          default:
            if (import.meta.env.DEV) {
              console.warn('Unmapped format error', issue)
            }
        }
      } else if (import.meta.env.DEV) {
        console.warn('Unmapped error', issue)
      }

      return t('validation.invalid')
    }
  })
}
