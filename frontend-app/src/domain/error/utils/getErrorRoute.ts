import type {RouteLocation, RouteLocationRaw} from "vue-router";
import {type AxiosError, isAxiosError} from "axios";
import {router} from "@/plugins/router.ts";
import {ErrorRoutes} from "@/domain/error/router/ErrorRoutes.ts";

export function getErrorRoute(errorOrOrigin?: RouteLocation | AxiosError): RouteLocationRaw
export function getErrorRoute(origin: RouteLocation, error: AxiosError): RouteLocationRaw
export function getErrorRoute(...args: Array<RouteLocation | AxiosError | undefined>): RouteLocationRaw {
  let origin: RouteLocation = router.currentRoute.value
  let errorStatus = 500

  while (args.length) {
    const arg = args.shift()

    if (isAxiosError(arg)) {
      if (arg.status) {
        errorStatus = arg.status
      }
    } else if (typeof arg === 'object') {
      origin = arg as RouteLocation
    }
  }

  return {
    name: ErrorRoutes.ERROR,
    params: {
      pathMatch: origin.path.substring(1).split('/')
    },
    query: origin.query,
    hash: origin.hash,
    state: {errorStatus},
    replace: true,
    force: true
  }
}
