import {createRouter, createWebHistory} from 'vue-router'
import {authRoutes} from "@/domain/auth/router/routes.ts";
import {listRoutes} from "@/domain/lists/router/routes.ts";
import {authGuard} from "@/domain/auth/router/AuthGuard.ts";
import {errorRoutes} from "@/domain/error/router/routes.ts";
import {ListRoutes} from "@/domain/lists/router/ListRoutes.ts";
import ApplicationShell from "@/layouts/ApplicationShell.vue";
import {templateRoutes} from "@/domain/templates/routes/routes.ts";
import {accountRoutes} from "@/domain/account/router/routes.ts";

export const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    ...authRoutes,
    ...errorRoutes,
    {
      path: '/',
      redirect: {name: ListRoutes.LIST},
      component: ApplicationShell,
      children: [
        ...listRoutes,
        ...templateRoutes,
        ...accountRoutes
      ]
    }
  ],
})

router.beforeEach(authGuard)
