import {expect, test} from "@fixtures";
import {RegisterPage} from "@pages/auth/RegisterPage.ts";
import {faker} from "@faker-js/faker";

test.describe("Register", () => {
    test("should register with valid data", async ({page}) => {
        await page.goto("/register")
        const registerPage = new RegisterPage(page)
        await registerPage.register(faker.internet.username(), faker.internet.email(), "password")
        await expect(page).toHaveURL("/lists")
    })
})