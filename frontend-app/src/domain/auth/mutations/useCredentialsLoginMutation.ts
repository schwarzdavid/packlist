import {AuthApi, type AuthDto, type CredentialsDto} from "@packlist/users-api";
import {useMutation, type UseMutationReturn} from "@pinia/colada";
import {useAuthStore} from "@/domain/auth/stores/AuthStore.ts";
import {toResponseData} from "@/utils/toResponseData.ts";

export function useCredentialsLoginMutation(): UseMutationReturn<AuthDto, CredentialsDto, Error> {
  const authApi = new AuthApi()
  const authStore = useAuthStore()

  return useMutation({
    mutation(credentials: CredentialsDto) {
      return authApi.login({
        body: credentials,
      }).then(toResponseData)
    },
    onSuccess(authDto) {
      authStore.setAuth(authDto)
    }
  })
}
