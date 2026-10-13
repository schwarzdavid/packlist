import {Locator, Page} from "@playwright/test";

export class RegisterPage {
    constructor(
        private readonly page: Page
    ) {
    }

    get usernameInput(): Locator {
        return this.page.getByTestId("username").getByRole("textbox")
    }

    get emailInput(): Locator {
        return this.page.getByTestId("email").getByRole("textbox")
    }

    get passwordInput(): Locator {
        return this.page.getByTestId("password").getByRole("textbox")
    }

    get passwordRepeatInput(): Locator {
        return this.page.getByTestId("password-repeat").getByRole("textbox")
    }

    get errorBanner(): Locator {
        return this.page.getByTestId("error-banner")
    }

    async register(username: string, email: string, password: string, passwordRepeat?: string) {
        await this.usernameInput.fill(username)
        await this.emailInput.fill(email)
        await this.passwordInput.fill(password)
        await this.passwordRepeatInput.fill(passwordRepeat ?? password)
        await this.passwordInput.press("Enter")
    }
}