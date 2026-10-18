import {type Client, createClient} from "@packlist/users-api";
import {useAuthStore} from "@/domain/auth/stores/AuthStore.ts";

interface ApiClassConstructor {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  new(options: { client?: Client }): any
}

let client: Client | null = null

export function useApi<T extends ApiClassConstructor>(ApiClass: T): InstanceType<T> {
  const client = ensureClient()
  return new ApiClass({client})
}

function ensureClient(): Client {
  if (!client) {
    const authStore = useAuthStore()
    client = createClient({
      auth(auth) {
        console.log(auth)
        if (auth.scheme !== 'bearer') {
          return
        }
        return authStore.token ?? undefined
      }
    })
  }

  return client
}
