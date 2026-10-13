import {test as baseTest} from "@fixtures/baseUrl.ts";
import {AuthDto, type UserDto} from "@packlist/users-api";
import {faker} from "@faker-js/faker";

export type CreateUserFn = (password: string) => Promise<UserDto>

export interface UserUtils {
    create: CreateUserFn
}

export interface UserFixture {
    user: UserUtils
}

export const test = baseTest.extend<UserFixture>({
    user: async ({request}, use) => {
        function createUser(password: string) {
            return request.post<AuthDto>("/api/auth/register", {
                data: {
                    username: faker.internet.username(),
                    email: faker.internet.email(),
                    password
                }
            }).then(async response => {
                if (response.status() === 201) {
                    return response.json().then(auth => auth.user)
                }
                const body = await response.body().then(value => value.toString())
                throw new Error(`Error ${response.status()}: ${body}`)
            })
        }

        await use({
            create: createUser
        })
    }
})