import {defineStore} from "pinia";
import {useQueryCache, useQueryState} from "@pinia/colada";
import type {AuthDto, UserDto} from "@packlist/users-api";
import {useLocalStorage} from "@vueuse/core";
import {readonly} from "vue";

export const ACCESS_TOKEN_KEY = 'STOW_ACCESS_TOKEN'

export const useAuthStore = defineStore('auth', () => {
  const cache = useQueryCache()
  const {data: user, isPending: isUserPending} = useQueryState<UserDto>(['auth', 'me'])
  const token = useLocalStorage<string | null>(ACCESS_TOKEN_KEY, null)

  function setAuth(authDto: AuthDto) {
    token.value = authDto.token
    cache.setQueryData(['auth', 'me'], authDto.user)
  }

  return {
    user,
    token: readonly(token),
    setAuth
  }
})
