import {AuthApi, type AuthDto, type CredentialsDto} from "@packlist/users-api";
import {useMutation, type UseMutationReturn, useQueryCache} from "@pinia/colada";
import {ACCESS_TOKEN_KEY} from "@/domain/auth/stores/AuthStore.ts";

export function useCredentialsLoginMutation(): UseMutationReturn<AuthDto, CredentialsDto, Error> {
  const authApi = new AuthApi()
  const cache = useQueryCache()

  return useMutation({
    mutation(credentials: CredentialsDto) {
      return authApi.login({
        body: credentials,
      })
    },
    onSuccess(authDto) {
      cache.setQueryData(['auth', 'me'], authDto.user)
      localStorage.setItem(ACCESS_TOKEN_KEY, authDto.token)
    }
  })
}
