import {defineStore} from "pinia";
import {useQueryState} from "@pinia/colada";
import type {UserDto} from "@packlist/users-api";
import {useLocalStorage} from "@vueuse/core";
import {readonly} from "vue";

export const ACCESS_TOKEN_KEY = 'STOW_ACCESS_TOKEN'

export const useAuthStore = defineStore('auth', () => {
  const {data: user, isPending: isUserPending} = useQueryState<UserDto>(['auth', 'me'])
  const token = useLocalStorage<string | null>(ACCESS_TOKEN_KEY, null)

  return {
    user,
    token: readonly(token)
  }
})
