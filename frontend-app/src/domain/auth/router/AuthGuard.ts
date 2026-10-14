import type {NavigationGuard, RouteLocationNormalizedGeneric, RouteLocationRaw} from "vue-router";
import {useQueryCache} from "@pinia/colada";
import {useCurrentUserQuery} from "@/domain/auth/queries/useCurrentUserQuery.ts";
import {HttpStatusCode, isAxiosError} from "axios";
import {AuthRoutes} from "@/domain/auth/router/AuthRoutes.ts";
import {useAuthStore} from "@/domain/auth/stores/AuthStore.ts";

export const authGuard: NavigationGuard = async (to) => {
  if (!to.meta.requiresAuth) {
    return
  }

  const authStore = useAuthStore()
  if (!authStore.token) {
    return getLoginLocation(to)
  }

  const cache = useQueryCache()
  const entry = cache.ensure(useCurrentUserQuery())

  try {
    await cache.refresh(entry)
  } catch (err) {
    if (isAxiosError(err) && err.status === HttpStatusCode.Unauthorized) {
      return getLoginLocation(to)
    }
    // TODO: redirect to error page. Until then proceed to next route
    throw err
  }
}

function getLoginLocation(to: RouteLocationNormalizedGeneric): RouteLocationRaw {
  return {
    name: AuthRoutes.LOGIN,
    query: {
      redirect: to.fullPath
    }
  }
}
