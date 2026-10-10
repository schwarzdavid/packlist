import {Locator, Page} from "@playwright/test";

export class LoginPage {
    constructor(
        private readonly page: Page
    ) {
    }

    get identifierInput(): Locator {
        return this.page.getByTestId("identifier").getByRole("textbox")
    }

    get passwordInput(): Locator {
        return this.page.getByTestId("password").getByRole("textbox")
    }

    get errorBanner(): Locator {
        return this.page.getByTestId("error-banner")
    }

    async login(identifier: string, password: string) {
        await this.identifierInput.fill(identifier)
        await this.passwordInput.fill(password)
        await this.page.getByTestId("submit").click()
    }
}