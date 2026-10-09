import {DockerComposeEnvironment, Wait} from "testcontainers";
import {resolve} from 'node:path'

export default async () => {
    const compose = await new DockerComposeEnvironment(resolve('../'), ['compose-e2e.yml'])
        .withBuild()
        .withWaitStrategy("frontend", Wait.forHttp("/", 80).forStatusCode(200))
        .up()

    process.env.FRONTEND_PORT = compose.getContainer('frontend').getMappedPort(80).toString()

    return () => compose.down()
}