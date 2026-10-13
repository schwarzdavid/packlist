import {AuthApi, type AuthDto, type RegistrationDto} from "@packlist/users-api";
import {useMutation, type UseMutationReturn} from "@pinia/colada";
import {useAuthStore} from "@/domain/auth/stores/AuthStore.ts";
import {toResponseData} from "@/utils/toResponseData.ts";

export function useRegistrationMutation(): UseMutationReturn<AuthDto, RegistrationDto, Error> {
  const authApi = new AuthApi()
  const authStore = useAuthStore()

  return useMutation({
    mutation(registration: RegistrationDto) {
      return authApi.register({
        body: registration
      }).then(toResponseData)
    },
    onSuccess(authDto) {
      authStore.setAuth(authDto)
    }
  })
}
