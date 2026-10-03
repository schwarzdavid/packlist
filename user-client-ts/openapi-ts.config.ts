import {defineConfig} from "@hey-api/openapi-ts";

export default defineConfig({
    input: '../user-api/src/main/resources/users-api.yml',
    output: 'src/api',
    parser: {
        transforms: {
            readWrite: false
        }
    },
    plugins: [
        {
            name: '@hey-api/sdk',
            operations: {
                strategy: 'byTags',
                containerName: '{{name}}Api'
            },
            transformer: true
        },
        '@hey-api/client-axios',
        {
            name: 'zod',
            definitions: true
        }
    ]
})