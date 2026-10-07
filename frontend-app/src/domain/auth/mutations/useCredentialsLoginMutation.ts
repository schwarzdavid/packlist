import {AuthApi, type AuthDto, type CredentialsDto} from "@packlist/users-api";
import {useMutation, type UseMutationReturn} from "@pinia/colada";
import {useAuthStore} from "@/domain/auth/stores/AuthStore.ts";

export function useCredentialsLoginMutation(): UseMutationReturn<AuthDto, CredentialsDto, Error> {
  const authApi = new AuthApi()
  const authStore = useAuthStore()

  return useMutation({
    mutation(credentials: CredentialsDto) {
      return authApi.login({
        body: credentials,
      })
    },
    onSuccess(authDto) {
      authStore.setAuth(authDto)
    }
  })
}
