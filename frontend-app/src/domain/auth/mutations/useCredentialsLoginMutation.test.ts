import {afterEach, describe, expect, vi} from "vitest";
import {useCredentialsLoginMutation} from "@/domain/auth/mutations/useCredentialsLoginMutation.ts";
import {useQueryCache} from "@pinia/colada";
import {AuthApi} from "@packlist/users-api";
import {ACCESS_TOKEN_KEY} from "@/domain/auth/stores/AuthStore.ts";
import {it} from "@test/fixtures/colada.ts";

describe(useCredentialsLoginMutation, () => {
  afterEach(() => {
    localStorage.clear()
    vi.restoreAllMocks()
  })

  it('should set all caches on successful login', async ({app, pinia}) => {
    vi.spyOn(AuthApi.prototype, 'login').mockResolvedValue({
      userId: 1,
      token: 'token',
      user: {
        userId: 1,
        username: 'foo',
        email: 'foo@bar.com'
      }
    })

    const mutation = app.runWithContext(() => useCredentialsLoginMutation())

    const result = await mutation.mutateAsync({
      identifier: 'foo',
      password: '12345678'
    })
    const queryCache = useQueryCache(pinia)

    expect(queryCache.getQueryData(['auth', 'me'])).toEqual({
      userId: 1,
      username: 'foo',
      email: 'foo@bar.com'
    })
    expect(result).toEqual({
      userId: 1,
      token: 'token',
      user: {
        userId: 1,
        username: 'foo',
        email: 'foo@bar.com'
      }
    })
    expect(localStorage.getItem(ACCESS_TOKEN_KEY)).toEqual("token")
  })

  it('should not set caches on failed login', async ({app, pinia}) => {
    vi.spyOn(AuthApi.prototype, 'login').mockRejectedValue(new Error('Invalid credentials'))

    const mutation = app.runWithContext(() => useCredentialsLoginMutation())
    const cache = useQueryCache(pinia)

    await expect(mutation.mutateAsync({
      identifier: 'foo',
      password: '12345678'
    })).rejects.toThrow('Invalid credentials')
    expect(cache.getQueryData(['auth', 'me'])).toBeUndefined()
    expect(localStorage.getItem(ACCESS_TOKEN_KEY)).toBeNull()
  })
})
