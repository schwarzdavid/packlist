import {expect, test} from "@fixtures";
import {LoginPage} from "@pages/auth/LoginPage.ts";

test.describe("Login", () => {
    test("should not login with invalid credentials", async ({page}) => {
        await page.goto("/login")
        const loginPage = new LoginPage(page)
        await loginPage.login("non-existing@email.com", "password")

        await expect(loginPage.errorBanner).toBeVisible()
    })

    test("should login with valid credentials", async ({page, user}) => {
        const {email} = await user.create("password")
        await page.goto("/login")

        const loginPage = new LoginPage(page)
        await loginPage.login(email, "password")

        await expect(page).toHaveURL("/lists")
    })
})