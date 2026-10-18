import {defineConfig} from 'vite'
import {heyApiPlugin} from "@hey-api/vite-plugin";
import dts from 'vite-plugin-dts'

export default defineConfig({
    plugins: [heyApiPlugin(), dts({bundleTypes: true})],
    build: {
        lib: {
            entry: 'src/index.ts',
            name: 'users-api',
            formats: ['es']
        },
        rolldownOptions: {
            external: ['axios', 'zod']
        }
    }
})