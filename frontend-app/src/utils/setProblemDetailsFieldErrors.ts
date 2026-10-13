import type {ProblemDetails} from "@packlist/users-api";
import type {AxiosError} from "axios";
import type {FormActions, GenericObject, Path} from "vee-validate";
import type {ComposerTranslation} from "vue-i18n";
import {i18n} from "@/plugins/i18n.ts";

export function setProblemDetailsFieldErrors<TValues extends GenericObject, TOutput = TValues>(err: AxiosError<ProblemDetails>, setFieldError: FormActions<TValues, TOutput>['setFieldError'], t?: ComposerTranslation) {
  if (!err.response || !err.response.data.fields) {
    return
  }
  t ??= i18n.global.t
  Object.entries(err.response.data.fields).forEach(([field, value]) => {
    setFieldError(field as Path<TValues>, t(value.error, value.params ?? {}))
  })
}
