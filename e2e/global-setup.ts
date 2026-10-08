import {DockerComposeEnvironment} from "testcontainers";
import {resolve} from 'node:path'

export default async () => {
    const compose = await new DockerComposeEnvironment(resolve('../'), ['compose.e2e.yml'])
        .withBuild()
        .up()

    return () => compose.down()
}