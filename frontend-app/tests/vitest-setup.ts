import '@testing-library/jest-dom/vitest'
import {vi} from 'vitest'

vi.stubGlobal('ResizeObserver', class {
  disconnect = vi.fn()
  observe = vi.fn()
})
