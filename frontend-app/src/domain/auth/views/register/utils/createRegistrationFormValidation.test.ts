import {beforeAll, describe, expect, it} from "vitest";
import {
  createRegistrationFormValidation
} from "@/domain/auth/views/register/utils/createRegistrationFormValidation.ts";
import type {ComposerTranslation} from "vue-i18n";
import {configureZod} from "@/config/configureZod.ts";
import {type ZodObject, type ZodString} from "zod";
import type {zRegistrationDto} from "@packlist/users-api";

describe(createRegistrationFormValidation, () => {
  const t: ComposerTranslation = (key: string) => key
  let validation: typeof zRegistrationDto & ZodObject<{ passwordRepeat: ZodString }>;

  beforeAll(() => {
    configureZod(t)
    validation = createRegistrationFormValidation(t)
  })

  it('should pass valid data', () => {
    const result = validation.safeParse({
      username: 'foo',
      email: 'foo@bar.com',
      password: '12345678',
      passwordRepeat: '12345678'
    })

    expect(result.success).toBe(true)
  })

  it('should require passwordRepeat', () => {
    const result = validation.safeParse({
      username: 'foo',
      email: 'foo@bar.com',
      password: '12345678'
    })

    expect(result.error?.issues[0]?.path).toContain('passwordRepeat')
    expect(result.error?.issues[0]?.message).toBe('validation.required')
  })

  it('should expect same password', () => {
    const result = validation.safeParse({
      username: 'foo',
      email: 'foo@bar.com',
      password: '12345678',
      passwordRepeat: 'baz'
    })

    expect(result.error?.issues[0]?.path).toContain('passwordRepeat')
    expect(result.error?.issues[0]?.message).toBe('validation.samePassword')
  })
})
