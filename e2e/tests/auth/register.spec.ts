import {expect, test} from "@fixtures";
import {RegisterPage} from "@pages/auth/RegisterPage.ts";

test.describe("Register", () => {
    test("should register with valid data", async ({page}) => {
        await page.goto("/register")
        const registerPage = new RegisterPage(page)
        await registerPage.register("test", "test@email.com", "password")
        await expect(page).toHaveURL("/lists")
    })
})