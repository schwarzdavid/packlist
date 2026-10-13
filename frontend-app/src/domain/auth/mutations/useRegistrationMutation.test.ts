import {afterEach, describe, expect, vi} from "vitest";
import {useRegistrationMutation} from "@/domain/auth/mutations/useRegistrationMutation.ts";
import {it} from "@test/fixtures/colada.ts";
import {AuthApi, type AuthDto} from "@packlist/users-api";
import {useQueryCache} from "@pinia/colada";
import {ACCESS_TOKEN_KEY} from "@/domain/auth/stores/AuthStore.ts";
import type {AxiosResponse} from "axios";

describe(useRegistrationMutation, () => {
  afterEach(() => {
    localStorage.clear()
    vi.restoreAllMocks()
  })

  it('should register user', async ({app, pinia}) => {
    vi.spyOn(AuthApi.prototype, 'register').mockResolvedValue({
      data: {
        userId: 1,
        token: 'token',
        user: {
          userId: 1,
          username: 'username',
          email: 'email@example.com'
        }
      }
    } as AxiosResponse<AuthDto>)

    const mutation = app.runWithContext(() => useRegistrationMutation())
    const cache = useQueryCache(pinia)

    const result = await mutation.mutateAsync({
      username: 'username',
      email: 'email@example.com',
      password: '12345678'
    })

    expect(cache.getQueryData(['auth', 'me'])).toEqual({
      userId: 1,
      username: 'username',
      email: 'email@example.com'
    })
    expect(result).toEqual({
      userId: 1,
      token: 'token',
      user: {
        userId: 1,
        username: 'username',
        email: 'email@example.com'
      }
    })
    expect(localStorage.getItem(ACCESS_TOKEN_KEY)).toEqual('token')
  })

  it('should not set caches if registration fails', async ({app, pinia}) => {
    vi.spyOn(AuthApi.prototype, 'register').mockRejectedValue(new Error('Registration failed'))

    const mutation = app.runWithContext(() => useRegistrationMutation())
    const cache = useQueryCache(pinia)

    await expect(mutation.mutateAsync({
      username: 'username',
      email: 'foo@bar.com',
      password: '12345678'
    })).rejects.toThrow('Registration failed')
    expect(cache.getQueryData(['auth', 'me'])).toBeUndefined()
    expect(localStorage.getItem(ACCESS_TOKEN_KEY)).toBeNull()
  })
})
