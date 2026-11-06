import type {RouteRecordRaw} from "vue-router";
import {TemplateRoutes} from "@/domain/templates/routes/TemplateRoutes.ts";
import TemplateListView from "@/domain/templates/views/list/TemplateListView.vue";
import TemplateDetailView from "@/domain/templates/views/details/TemplateDetailView.vue";

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
    path: '/templates/:templateId',
    name: TemplateRoutes.DETAILS,
    component: TemplateDetailView,
    meta: {
      requiresAuth: true
    }
  }
]
