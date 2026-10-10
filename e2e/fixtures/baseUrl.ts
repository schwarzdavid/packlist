import {test as baseTest} from "@playwright/test";

export const test = baseTest.extend({
    baseURL: async ({}, use) => {
        const port = process.env.FRONTEND_PORT
        if (!port) {
            throw new Error('FRONTEND_PORT is not set')
        }
        await use(new URL(`http://localhost:${port}`).toString())
    }
})

