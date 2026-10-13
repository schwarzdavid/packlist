import {defineConfig} from "@hey-api/openapi-ts";

export default defineConfig({
    input: '../user-api/src/main/resources/users-api.yml',
    output: 'src/api',
    parser: {
        transforms: {
            readWrite: false,
        }
    },
    plugins: [
        {
            name: '@hey-api/transformers',
            bigInt: false
        },
        {
            name: '@hey-api/sdk',
            operations: {
                strategy: 'byTags',
                methods: 'instance',
                containerName: '{{name}}Api'
            },
            responseStyle: 'data',
            transformer: true,
        },
        {
            name: '@hey-api/client-fetch',
            throwOnError: true
        },
        {
            name: 'zod',
            definitions: true,
            requests: false,
            responses: false,
            includeInEntry: true
        }
    ]
})