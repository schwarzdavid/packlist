import {AuthApi, type AuthDto, type RegistrationDto} from "@packlist/users-api";
import {useMutation, type UseMutationReturn, useQueryCache} from "@pinia/colada";
import {ACCESS_TOKEN_KEY} from "@/domain/auth/stores/AuthStore.ts";

export function useRegistrationMutation(): UseMutationReturn<AuthDto, RegistrationDto, Error> {
  const authApi = new AuthApi()
  const cache = useQueryCache()

  return useMutation({
    mutation(registration: RegistrationDto) {
      return authApi.register({
        body: registration
      })
    },
    onSuccess(authDto) {
      cache.setQueryData(['auth', 'me'], authDto.user)
      localStorage.setItem(ACCESS_TOKEN_KEY, authDto.token)
    }
  })
}
