import type {RouteRecordRaw} from "vue-router";
import {TemplateRoutes} from "@/domain/templates/routes/TemplateRoutes.ts";
import TemplateListView from "@/domain/templates/views/list/TemplateListView.vue";
import CreateTemplateView from "@/domain/templates/views/create/CreateTemplateView.vue";

export const templateRoutes: RouteRecordRaw[] = [
  {
    path: '/templates',
    name: TemplateRoutes.LIST,
    component: TemplateListView,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/templates/create',
    name: TemplateRoutes.CREATE,
    component: CreateTemplateView,
    meta: {
      requiresAuth: true
    }
  }
]
