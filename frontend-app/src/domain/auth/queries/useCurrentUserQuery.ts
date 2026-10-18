import {UserApi} from "@packlist/users-api";
import {defineQueryOptions} from "@pinia/colada";
import {toResponseData} from "@/utils/toResponseData.ts";
import {useApi} from "@/composables/useApi.ts";

export function useCurrentUserQuery() {
  const userApi = useApi(UserApi)

  return defineQueryOptions({
    key: ['auth', 'me'],
    query: () => userApi.getCurrentUser().then(toResponseData)
  })
}
