import {expect, test} from "@fixtures";
import {LoginPage} from "@pages/auth/LoginPage.ts";

test.describe("Login", () => {
    test("should not login with invalid credentials", async ({page}) => {
        await page.goto("/login")
        const loginPage = new LoginPage(page)
        await loginPage.login("non-existing@email.com", "password")

        await expect(loginPage.errorBanner).toBeVisible()
        await expect(loginPage.errorBanner).toHaveText("Invalid credentials")
    })

    test("should login with valid credentials", async ({page, user}) => {
        const {email} = await user.create("password")
        await page.goto("/login")

        const loginPage = new LoginPage(page)
        await loginPage.login(email, "password")

        await expect(page).toHaveURL("/lists")
    })

    test("should show generic error if connection fails", async ({page}) => {
        await page.goto("/login")
        await page.route("**/api/auth/login", route => route.abort("internetdisconnected"))

        const loginPage = new LoginPage(page)
        await loginPage.login("user", "password");

        await expect(loginPage.errorBanner).toBeVisible()
        await expect(loginPage.errorBanner).not.toHaveText("Invalid credentials");
    })
})