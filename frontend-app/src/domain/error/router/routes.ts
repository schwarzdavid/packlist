import type {RouteRecordRaw} from "vue-router";
import {ErrorRoutes} from "@/domain/error/router/ErrorRoutes.ts";
import ErrorView from "@/domain/error/views/ErrorView.vue";

export const errorRoutes: RouteRecordRaw[] = [
  {
    name: ErrorRoutes.ERROR,
    path: '/:pathMatch(.*)*',
    component: ErrorView,
    props: () => ({
      status: window.history.state?.errorStatus ?? 404
    })
  }
]
