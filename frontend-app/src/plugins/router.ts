import {createRouter, createWebHistory} from 'vue-router'
import {authRoutes} from "@/domain/auth/router/routes.ts";
import {listRoutes} from "@/domain/lists/router/routes.ts";
import {authGuard} from "@/domain/auth/router/AuthGuard.ts";

export const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    ...authRoutes,
    ...listRoutes,
  ],
})

router.beforeEach(authGuard)
