import {UserApi} from "@packlist/users-api";
import {defineQueryOptions} from "@pinia/colada";
import {toResponseData} from "@/utils/toResponseData.ts";

export function useCurrentUserQuery() {
  const userApi = new UserApi()

  return defineQueryOptions({
    key: ['auth', 'me'],
    query: () => userApi.getCurrentUser().then(toResponseData)
  })
}
