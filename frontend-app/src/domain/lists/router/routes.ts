import type {RouteRecordRaw} from "vue-router";
import {ListRoutes} from "@/domain/lists/router/ListRoutes.ts";
import ListView from "@/domain/lists/views/ListView.vue";

export const listRoutes: RouteRecordRaw[] = [
  {
    path: "/lists",
    name: ListRoutes.LIST,
    component: ListView,
    meta: {
      requiresAuth: true
    }
  }
]
