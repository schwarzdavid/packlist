import {defineStore} from "pinia";
import {useQueryState} from "@pinia/colada";
import type {UserDto} from "@packlist/users-api";
import {useLocalStorage} from "@vueuse/core";

export const useAuthStore = defineStore('auth', () => {
  const {data: user, isPending: isUserPending} = useQueryState<UserDto>(['auth', 'me'])
  const token = useLocalStorage<string | null>('STOW_ACCESS_TOKEN', null)


  return {
    user,
    token
  }
})
