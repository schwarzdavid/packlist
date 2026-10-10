import {test as baseTest} from "@fixtures/baseUrl.ts";
import {type UserDto} from "@packlist/users-api";

export type CreateUserFn = (username: string, email: string, password: string) => Promise<UserDto>

export interface CreateUserFixture {
    user: CreateUserFn
}

export const test = baseTest.extend<CreateUserFixture>({
    user: async ({request}, use) => {
        function createUser(username: string, email: string, password: string) {
            return request.post<UserDto>("/api/auth/register", {
                data: {
                    username,
                    email,
                    password
                }
            }).then(response => response.json())
        }

        await use(createUser)
    }
})