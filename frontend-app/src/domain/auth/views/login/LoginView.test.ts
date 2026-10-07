import {describe, expect, it, vi} from "vitest";
import {createI18n} from "vue-i18n";
import {createVuetify} from "vuetify/framework";
import {configureZod} from "@/config/configureZod.ts";
import {fireEvent, render, waitFor} from "@testing-library/vue";
import {RouterLinkStub} from "@vue/test-utils";
import LoginView from "@/domain/auth/views/login/LoginView.vue";
import {ListRoutes} from "@/domain/lists/router/ListRoutes.ts";

const mutationMock = vi.hoisted(() => ({
  mutateAsync: vi.fn()
}))

const routerMock = vi.hoisted(() => ({
  push: vi.fn()
}))

vi.mock('@/domain/auth/mutations/useCredentialsLoginMutation.ts', () => ({
  useCredentialsLoginMutation: () => mutationMock
}))

vi.mock('vue-router', () => ({
  useRouter: () => routerMock
}))

describe('LoginView', () => {
  it('should login user with correct credentials', async () => {
    const wrapper = mountView()
    const {promise, resolve} = Promise.withResolvers()

    mutationMock.mutateAsync.mockReturnValue(promise)

    const identifierInput = wrapper.getByTestId("identifier").querySelector('input')!
    const passwordInput = wrapper.getByTestId("password").querySelector('input')!
    const submitButton = wrapper.getByTestId("submit")

    await Promise.all([
      fireEvent.update(identifierInput, "username"),
      fireEvent.update(passwordInput, "password")
    ])

    await fireEvent.submit(wrapper.getByTestId("form"))

    expect(identifierInput).toBeDisabled()
    expect(passwordInput).toBeDisabled()

    await waitFor(() => expect(submitButton.getAttribute("aria-busy")).toBe("true"))

    resolve({})

    await waitFor(() => expect(mutationMock.mutateAsync).toHaveBeenCalledWith({
      identifier: "username",
      password: "password"
    }))
    await waitFor(() => expect(submitButton.getAttribute("aria-busy")).toBeNull())

    expect(identifierInput).not.toBeDisabled()
    expect(passwordInput).not.toBeDisabled()

    await waitFor(() => expect(routerMock.push).toHaveBeenCalledWith({name: ListRoutes.LIST}))
  })

  it('should show error messages on invalid input', async () => {
    const wrapper = mountView()

    const identifierInput = wrapper.getByTestId("identifier").querySelector('input')!
    const passwordInput = wrapper.getByTestId("password").querySelector('input')!

    await fireEvent.submit(wrapper.getByTestId("form"))

    await waitFor(() => expect(identifierInput).toHaveAccessibleDescription("validation.required"))
    expect(passwordInput).toHaveAccessibleDescription("validation.required")
    expect(mutationMock.mutateAsync).not.toHaveBeenCalled()
    expect(routerMock.push).not.toHaveBeenCalled()
    expect(wrapper.getByTestId("error-banner")).toBeNull()
  })

  it('should show error banner on unknown error', async () => {
    const wrapper = mountView()

    mutationMock.mutateAsync.mockRejectedValue(new TypeError("Failed to fetch"))

    const identifierInput = wrapper.getByTestId("identifier").querySelector('input')!
    const passwordInput = wrapper.getByTestId("password").querySelector('input')!

    await Promise.all([
      fireEvent.update(identifierInput, "username"),
      fireEvent.update(passwordInput, "password"),
    ])

    await fireEvent.submit(wrapper.getByTestId("form"))

    await waitFor(() => expect(wrapper.getByTestId("error-banner")).toBeInTheDocument())
    expect(wrapper.getByTestId("error-banner").textContent).not.toEqual("")
  })

  it.todo("should show error message on invalid credentials")
})

function mountView() {
  const i18n = createI18n({legacy: false, locale: 'en', missingWarn: false, fallbackWarn: false})
  const vuetify = createVuetify()

  configureZod(i18n.global.t)

  return render(LoginView, {
    global: {
      plugins: [i18n, vuetify],
      stubs: {RouterLink: RouterLinkStub}
    }
  })
}
