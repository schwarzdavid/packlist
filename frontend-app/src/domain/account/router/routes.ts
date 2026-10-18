import type {RouteRecordRaw} from "vue-router";
import {AccountRoutes} from "@/domain/account/router/AccountRoutes.ts";
import AccountView from "@/domain/account/views/AccountView.vue";

export const accountRoutes: RouteRecordRaw[] = [
  {
    path: '/account',
    name: AccountRoutes.ACCOUNT,
    component: AccountView,
    meta: {
      requiresAuth: true
    }
  }
]
