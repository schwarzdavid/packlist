import {beforeAll, describe, expect, it} from "vitest";
import {configureZod} from "@/config/configureZod.ts";
import type {ComposerTranslation} from "vue-i18n";
import {z} from "zod";

describe(configureZod, () => {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const t: ComposerTranslation = (key: string, params?: any) => params ? `${key} ${Object.entries(params).map((entry) => entry.join(':')).join(',')}` : key

  beforeAll(() => configureZod(t))

  it('should map required field errors', () => {
    const nullResult = z.string().safeParse(null)
    const undefinedResult = z.string().safeParse(undefined)
    const emptyResult = z.string().safeParse(undefined)

    expect(nullResult.error?.issues[0]?.message).toBe('validation.required')
    expect(undefinedResult.error?.issues[0]?.message).toBe('validation.required')
    expect(emptyResult.error?.issues[0]?.message).toBe('validation.required')
  })

  it('should map minLength 1 to required field error', () => {
    const result = z.string().min(1).safeParse('')

    expect(result.error?.issues[0]?.message).toBe('validation.required')
  })

  it('should map too short strings', () => {
    const result = z.string().min(5).safeParse('foo')

    expect(result.error?.issues[0]?.message).toBe('validation.minLength min:5')
  })

  it('should map too long strings', () => {
    const result = z.string().max(5).safeParse('foobar')

    expect(result.error?.issues[0]?.message).toBe('validation.maxLength max:5')
  })

  it('should map invalid e-mail addresses', () => {
    const invalidResult = z.email().safeParse('test')
    const emptyResult = z.email().safeParse('')
    const nullResult = z.email().safeParse(null)
    const undefinedResult = z.email().safeParse(undefined)

    expect(invalidResult.error?.issues[0]?.message).toBe('validation.email')
    expect(emptyResult.error?.issues[0]?.message).toBe('validation.email')
    expect(nullResult.error?.issues[0]?.message).toBe('validation.required')
    expect(undefinedResult.error?.issues[0]?.message).toBe('validation.required')
  })

  it('should map known format errors', () => {
    const result = z.uuidv7().safeParse('test')

    expect(result.error?.issues[0]?.message).toBe('validation.invalid')
  })

  it('should map unknown error', () => {
    const result = z.custom(() => false).safeParse('test')

    expect(result.error?.issues[0]?.message).toBe('validation.invalid')
  })
})
