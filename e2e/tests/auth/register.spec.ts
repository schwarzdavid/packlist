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

    test('should show error hint if username is already taken', async ({page, user}) => {
        const {username} = await user.create('12345678')
        await page.goto("/register")

        const registerPage = new RegisterPage(page)
        await registerPage.register(username, faker.internet.email(), "password")

        await expect(registerPage.usernameInput).toHaveAccessibleDescription('Username is already taken')
    })

    test('should show generic error if connection fails', async ({page}) => {
        await page.goto("/register")
        await page.route("**/api/auth/register", route => route.abort("internetdisconnected"))

        const registerPage = new RegisterPage(page)
        await registerPage.register("user", "password", "12345678");

        await expect(registerPage.errorBanner).toBeVisible()
        await expect(registerPage.errorBanner).toContainText("Something went wrong");
    })
})