import type {RouteRecordRaw} from "vue-router";
import AuthLayout from "@/domain/auth/layouts/AuthLayout.vue";
import {AuthRoutes} from "@/domain/auth/router/AuthRoutes.ts";
import LoginView from "@/domain/auth/views/login/LoginView.vue";
import RegisterView from "@/domain/auth/views/register/RegisterView.vue";

export const authRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    component: AuthLayout,
    children: [
      {
        path: '',
        name: AuthRoutes.LOGIN,
        component: LoginView
      },
      {
        path: '/register',
        name: AuthRoutes.REGISTER,
        component: RegisterView
      }
    ]
  }
]
