import {afterEach, describe, expect, it, vi} from "vitest";
import RegisterView from "@/domain/auth/views/register/RegisterView.vue";
import {RouterLinkStub} from "@vue/test-utils";
import {createI18n} from "vue-i18n";
import {configureZod} from "@/config/configureZod.ts";
import {createVuetify} from "vuetify/framework";
import {fireEvent, render, waitFor} from "@testing-library/vue";
import type {AuthDto} from "@packlist/users-api";
import {ListRoutes} from "@/domain/lists/router/ListRoutes.ts";
import {createAxiosConflictError} from "@test/fixtures/axiosErrorUtils.ts";

const mutationMock = vi.hoisted(() => ({
  mutateAsync: vi.fn()
}))

const routerMock = vi.hoisted(() => ({
  push: vi.fn()
}))

vi.mock('@/domain/auth/mutations/useRegistrationMutation.ts', () => ({
  useRegistrationMutation: () => mutationMock
}))

vi.mock('vue-router', () => ({
  useRouter: () => routerMock
}))

describe("RegisterView", () => {
  afterEach(() => {
    vi.clearAllMocks()
  })

  it('should register when form is submitted', async () => {
    const wrapper = mountView()
    const {promise, resolve} = Promise.withResolvers<AuthDto>()

    mutationMock.mutateAsync.mockReturnValue(promise)

    const usernameInput = wrapper.getByTestId('username').querySelector("input")!
    const emailInput = wrapper.getByTestId('email').querySelector("input")!
    const passwordInput = wrapper.getByTestId('password').querySelector("input")!
    const passwordRepeatInput = wrapper.getByTestId("password-repeat").querySelector("input")!
    const submitButton = wrapper.getByTestId("submit")

    await Promise.all([
      fireEvent.update(usernameInput, "username"),
      fireEvent.update(emailInput, "foo@bar.com"),
      fireEvent.update(passwordInput, "password"),
      fireEvent.update(passwordRepeatInput, "password")
    ])


    await fireEvent.click(submitButton)

    await waitFor(() => expect(submitButton.getAttribute("aria-busy")).toBe("true"))
    expect(usernameInput.disabled).toBe(true)
    expect(emailInput.disabled).toBe(true)
    expect(passwordInput.disabled).toBe(true)
    expect(passwordRepeatInput.disabled).toBe(true)

    await waitFor(() => expect(mutationMock.mutateAsync).toHaveBeenCalledExactlyOnceWith({
      username: "username",
      email: "foo@bar.com",
      password: "password",
      passwordRepeat: "password"
    }))

    resolve({} as unknown as AuthDto)

    await waitFor(() => expect(submitButton.getAttribute("aria-busy")).toBe(null))

    expect(usernameInput.disabled).toBe(false)
    expect(emailInput.disabled).toBe(false)
    expect(passwordInput.disabled).toBe(false)
    expect(passwordRepeatInput.disabled).toBe(false)
    expect(wrapper.queryByTestId("error-banner")).toBeNull()

    await waitFor(() => expect(routerMock.push).toHaveBeenCalledExactlyOnceWith({name: ListRoutes.LIST}))
  })

  it('should show error messages on invalid types', async () => {
    const wrapper = mountView()

    const usernameInput = wrapper.getByTestId('username').querySelector("input")!
    const emailInput = wrapper.getByTestId('email').querySelector("input")!
    const passwordInput = wrapper.getByTestId('password').querySelector("input")!
    const passwordRepeatInput = wrapper.getByTestId("password-repeat").querySelector("input")!

    await fireEvent.click(wrapper.getByTestId("submit"))

    await waitFor(() => expect(usernameInput).toHaveAccessibleDescription("validation.required"))
    expect(emailInput).toHaveAccessibleDescription("validation.required")
    expect(passwordInput).toHaveAccessibleDescription("validation.required")
    expect(passwordRepeatInput).toHaveAccessibleDescription("validation.required")
    expect(mutationMock.mutateAsync).not.toHaveBeenCalled()
    expect(wrapper.queryByTestId("error-banner")).toBeNull()
  })

  it('should show error banner if mutation fails unexpectedly', async () => {
    mutationMock.mutateAsync.mockRejectedValue(new TypeError("Failed to fetch"))

    const wrapper = mountView()

    const usernameInput = wrapper.getByTestId('username').querySelector("input")!
    const emailInput = wrapper.getByTestId('email').querySelector("input")!
    const passwordInput = wrapper.getByTestId('password').querySelector("input")!
    const passwordRepeatInput = wrapper.getByTestId("password-repeat").querySelector("input")!

    await Promise.all([
      fireEvent.update(usernameInput, "username"),
      fireEvent.update(emailInput, "foo@bar.com"),
      fireEvent.update(passwordInput, "password"),
      fireEvent.update(passwordRepeatInput, "password")
    ])

    await fireEvent.click(wrapper.getByTestId('submit'))

    await waitFor(() => expect(wrapper.getByTestId("error-banner")).toBeInTheDocument())
  })

  it('should show error message if username is already taken', async () => {
    mutationMock.mutateAsync.mockRejectedValue(createAxiosConflictError('username'))

    const wrapper = mountView()

    const usernameInput = wrapper.getByTestId('username').querySelector("input")!
    const emailInput = wrapper.getByTestId('email').querySelector("input")!
    const passwordInput = wrapper.getByTestId('password').querySelector("input")!
    const passwordRepeatInput = wrapper.getByTestId("password-repeat").querySelector("input")!

    await Promise.all([
      fireEvent.update(usernameInput, "username"),
      fireEvent.update(emailInput, "foo@bar.com"),
      fireEvent.update(passwordInput, "password"),
      fireEvent.update(passwordRepeatInput, "password")
    ])

    await fireEvent.click(wrapper.getByTestId('submit'))

    await waitFor(() => expect(usernameInput).toHaveAccessibleDescription('validation.usernameTaken'))
  })

  it('should show error message if e-mail is already taken', async () => {
    mutationMock.mutateAsync.mockRejectedValue(createAxiosConflictError('email'))

    const wrapper = mountView()

    const usernameInput = wrapper.getByTestId('username').querySelector("input")!
    const emailInput = wrapper.getByTestId('email').querySelector("input")!
    const passwordInput = wrapper.getByTestId('password').querySelector("input")!
    const passwordRepeatInput = wrapper.getByTestId("password-repeat").querySelector("input")!

    await Promise.all([
      fireEvent.update(usernameInput, "username"),
      fireEvent.update(emailInput, "foo@bar.com"),
      fireEvent.update(passwordInput, "password"),
      fireEvent.update(passwordRepeatInput, "password")
    ])

    await fireEvent.click(wrapper.getByTestId('submit'))

    await waitFor(() => expect(emailInput).toHaveAccessibleDescription('validation.emailTaken'))
  })
})

function mountView() {
  const i18n = createI18n({legacy: false, locale: 'en', missingWarn: false, fallbackWarn: false})
  const vuetify = createVuetify()

  configureZod(i18n.global.t)

  return render(RegisterView, {
    global: {
      plugins: [i18n, vuetify],
      stubs: {RouterLink: RouterLinkStub}
    }
  })
}
