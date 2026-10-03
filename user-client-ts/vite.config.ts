import {defineConfig} from 'vite'
import {heyApiPlugin} from "@hey-api/vite-plugin";

export default defineConfig({
    plugins: [heyApiPlugin()],
    build: {
        lib: {
            entry: 'src/api/index.ts',
            name: 'users-api.ts',
            formats: ['es']
        },
        rolldownOptions: {
            external: ['axios', 'zod']
        }
    }
})