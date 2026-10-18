import {defineConfig} from "@hey-api/openapi-ts";

export default defineConfig({
    input: '../list-api/src/main/resources/lists-api.yml',
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
            transformer: true,
        },
        {
            name: '@hey-api/client-axios',
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