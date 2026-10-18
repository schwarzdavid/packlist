import type {RouteRecordRaw} from "vue-router";
import {ListRoutes} from "@/domain/lists/router/ListRoutes.ts";
import ListView from "@/domain/lists/views/list/ListView.vue";
import CreateListView from "@/domain/lists/views/create/CreateListView.vue";

export const listRoutes: RouteRecordRaw[] = [
  {
    path: "/lists",
    name: ListRoutes.LIST,
    component: ListView,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: "/lists/create",
    name: ListRoutes.CREATE,
    component: CreateListView,
    meta: {
      requiresAuth: true
    }
  }
]
