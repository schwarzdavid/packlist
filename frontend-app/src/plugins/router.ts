import {createRouter, createWebHistory} from 'vue-router'
import {authRoutes} from "@/domain/auth/router/routes.ts";

export const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    ...authRoutes
  ],
})

