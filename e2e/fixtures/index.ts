import {mergeTests} from "@playwright/test";
import {test as baseUrlTest} from "./baseUrl.js";
import {test as userTest} from "./user.js";

export const test = mergeTests(baseUrlTest, userTest)

export {expect} from "@playwright/test";