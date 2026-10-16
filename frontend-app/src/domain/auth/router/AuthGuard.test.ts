import {afterEach, describe, expect, vi} from "vitest";
import {authGuard} from "@/domain/auth/router/AuthGuard.ts";
import {it} from "@test/fixtures/colada.ts";
import {UserApi, type UserDto} from "@packlist/users-api";
import {type AxiosResponse} from "axios";
import {
  createMemoryHistory,
  createRouter,
  type RouteLocationNormalized,
  START_LOCATION
} from "vue-router";
import {useQueryCache} from "@pinia/colada";
import {ACCESS_TOKEN_KEY} from "@/domain/auth/stores/AuthStore.ts";
import {AuthRoutes} from "@/domain/auth/router/AuthRoutes.ts";
import {
  createAxiosInternalServerError,
  createAxiosUnauthorizedError
} from "@test/fixtures/axiosErrorUtils.ts";
import {ListRoutes} from "@/domain/lists/router/ListRoutes.ts";

describe(authGuard, () => {
  const router = createRouter({
    history: createMemoryHistory(),
    routes: [
      {path: '/public', component: {}, meta: {}, name: 'public'},
      {path: '/secured', component: {}, meta: {requiresAuth: true}, name: 'secured'},
      {path: '/default', component: {}, meta: {requiresAuth: true}, name: ListRoutes.LIST}
    ]
  })

  afterEach(() => {
    vi.restoreAllMocks()
    localStorage.clear()
  })

  it('should reload user on secured route if user is not cached', async ({app, pinia}) => {
    const apiSpy = vi.spyOn(UserApi.prototype, 'getCurrentUser').mockResolvedValue({
      data: {
        userId: 1,
        username: 'test',
        email: 'test@example.com'
      } satisfies UserDto
    } as AxiosResponse<UserDto>)

    localStorage.setItem(ACCESS_TOKEN_KEY, "token")

    const to = router.resolve('/secured') as RouteLocationNormalized
    const result = await app.runWithContext(() => authGuard(to, START_LOCATION, vi.fn()))
    const cache = useQueryCache(pinia)

    expect(result).toBeUndefined()
    expect(apiSpy).toHaveBeenCalledOnce()
    expect(cache.getQueryData(['auth', 'me'])).toBeDefined()
  })

  it('should not reload user on secured router if user is cached', async ({app, pinia}) => {
    const apiSpy = vi.spyOn(UserApi.prototype, 'getCurrentUser')
    localStorage.setItem(ACCESS_TOKEN_KEY, "token")

    const cache = useQueryCache(pinia)
    cache.setQueryData(['auth', 'me'], {
      userId: 1,
      username: 'test',
      email: 'test@example.com'
    } satisfies UserDto)

    const to = router.resolve('/secured') as RouteLocationNormalized
    const result = await app.runWithContext(() => authGuard(to, START_LOCATION, vi.fn()))

    expect(result).toBeUndefined()
    expect(apiSpy).not.toHaveBeenCalled()
  })

  it('should redirect to login if jwt is not set', async ({app}) => {
    const apiSpy = vi.spyOn(UserApi.prototype, 'getCurrentUser')

    const to = router.resolve('/secured') as RouteLocationNormalized
    const result = await app.runWithContext(() => authGuard(to, START_LOCATION, vi.fn()))

    expect(result).toEqual({
      name: AuthRoutes.LOGIN,
      query: {
        redirect: to.fullPath
      }
    })
    expect(apiSpy).not.toHaveBeenCalled()
  })

  it('should redirect to login if backend responds with unauthorized', async ({app}) => {
    const apiSpy = vi.spyOn(UserApi.prototype, 'getCurrentUser').mockRejectedValue(createAxiosUnauthorizedError())

    localStorage.setItem(ACCESS_TOKEN_KEY, "token")

    const to = router.resolve('/secured') as RouteLocationNormalized
    const result = await app.runWithContext(() => authGuard(to, START_LOCATION, vi.fn()))

    expect(result).toEqual({
      name: AuthRoutes.LOGIN,
      query: {
        redirect: to.fullPath
      }
    })
    expect(apiSpy).toHaveBeenCalledOnce()
  })

  it('should throw if backend responds with non unauthorized error code', async ({app}) => {
    const apiSpy = vi.spyOn(UserApi.prototype, 'getCurrentUser').mockRejectedValue(createAxiosInternalServerError())

    localStorage.setItem(ACCESS_TOKEN_KEY, "token")

    const to = router.resolve('/secured') as RouteLocationNormalized
    await expect(app.runWithContext(() => authGuard(to, START_LOCATION, vi.fn()))).rejects.toThrow()
    expect(apiSpy).toHaveBeenCalledOnce()
  })

  it('should allow public routes when user is not logged in', async ({app}) => {
    const apiSpy = vi.spyOn(UserApi.prototype, 'getCurrentUser')

    const to = router.resolve('/public') as RouteLocationNormalized
    const result = await app.runWithContext(() => authGuard(to, START_LOCATION, vi.fn()))

    expect(result).toBeUndefined()
    expect(apiSpy).not.toHaveBeenCalled()
  })

  it('should allow public routes when user is logged in', async ({app}) => {
    const apiSpy = vi.spyOn(UserApi.prototype, 'getCurrentUser')

    localStorage.setItem(ACCESS_TOKEN_KEY, "token")

    const cache = useQueryCache()
    cache.setQueryData(['auth', 'me'], {
      userId: 1,
      username: 'test',
      email: 'test@test.com'
    } satisfies UserDto)

    const to = router.resolve('/public') as RouteLocationNormalized
    const result = await app.runWithContext(() => authGuard(to, START_LOCATION, vi.fn()))

    expect(result).toBeUndefined()
    expect(apiSpy).not.toHaveBeenCalled()
  })

  it('should not add redirect param when target route is list route without query', async ({app}) => {
    const to = router.resolve({name: ListRoutes.LIST}) as RouteLocationNormalized
    const result = await app.runWithContext(() => authGuard(to, START_LOCATION, vi.fn()))

    expect(result).toEqual({
      name: AuthRoutes.LOGIN,
      query: {}
    })
  })

  it('should add redirect param when target route is list route with query', async ({app}) => {
    const to = router.resolve({
      name: ListRoutes.LIST,
      query: {foo: 'bar'}
    }) as RouteLocationNormalized
    const result = await app.runWithContext(() => authGuard(to, START_LOCATION, vi.fn()))

    expect(result).toEqual({
      name: AuthRoutes.LOGIN,
      query: {redirect: '/default?foo=bar'}
    })
  })
})
