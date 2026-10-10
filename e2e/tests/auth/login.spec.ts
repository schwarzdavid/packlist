import {expect, test} from "@fixtures";
import {LoginPage} from "@pages/auth/LoginPage.ts";

test.describe("Login", () => {
    test("should not login with invalid credentials", async ({page}) => {
        const loginPage = new LoginPage(page)
        await page.goto("/login")
        await loginPage.login("non-existing@email.com", "password")

        await expect(loginPage.errorBanner).toBeVisible()
    })
})