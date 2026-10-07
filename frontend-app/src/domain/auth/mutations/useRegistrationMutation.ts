import {AuthApi, type AuthDto, type RegistrationDto} from "@packlist/users-api";
import {useMutation, type UseMutationReturn} from "@pinia/colada";
import {useAuthStore} from "@/domain/auth/stores/AuthStore.ts";

export function useRegistrationMutation(): UseMutationReturn<AuthDto, RegistrationDto, Error> {
  const authApi = new AuthApi()
  const authStore = useAuthStore()

  return useMutation({
    mutation(registration: RegistrationDto) {
      return authApi.register({
        body: registration
      })
    },
    onSuccess(authDto) {
      authStore.setAuth(authDto)
    }
  })
}
