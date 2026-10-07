import {afterEach, describe, expect} from "vitest";
import {ACCESS_TOKEN_KEY, useAuthStore} from "@/domain/auth/stores/AuthStore.ts";
import {it} from '@test/fixtures/colada.ts'
import {useQueryCache} from "@pinia/colada";

describe(useAuthStore, () => {
  afterEach(() => {
    localStorage.clear()
  })

  it('should return the user from the query', ({app, pinia}) => {
    const authStore = app.runWithContext(() => useAuthStore())
    const cache = useQueryCache(pinia)

    const user = {userId: 1, username: 'user', email: 'foo@bar.com'}

    expect(authStore.user).toBeUndefined()

    cache.setQueryData(['auth', 'me'], user)

    expect(authStore.user).toEqual(user)
  })

  it('should listen to localStorage change event', ({app}) => {
    const authStore = app.runWithContext(() => useAuthStore())

    expect(authStore.token).toBeNull()

    localStorage.setItem(ACCESS_TOKEN_KEY, "token")
    window.dispatchEvent(new StorageEvent('storage', {
      key: ACCESS_TOKEN_KEY,
      newValue: 'token',
      oldValue: null,
      storageArea: localStorage
    }))

    expect(authStore.token).toEqual("token")
  })

  describe('setAuth', () => {
    it('should set the correct values from an AuthDto', ({app, pinia}) => {
      const authStore = app.runWithContext(() => useAuthStore())
      const cache = useQueryCache(pinia)

      expect(authStore.token).toBeNull()
      expect(authStore.user).toBeUndefined()

      authStore.setAuth({
        userId: 1,
        token: "token",
        user: {
          userId: 1,
          username: 'user',
          email: 'foo@bar.com'
        }
      })

      expect(authStore.token).toEqual("token")
      expect(authStore.user).toEqual({
        userId: 1,
        username: 'user',
        email: 'foo@bar.com'
      })
      expect(cache.getQueryData(['auth', 'me'])).toEqual({
        userId: 1,
        username: 'user',
        email: 'foo@bar.com'
      })
    })
  })
})
