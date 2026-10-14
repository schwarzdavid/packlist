import {DockerComposeEnvironment, Wait} from "testcontainers";
import {join, resolve} from 'node:path'

export default async () => {
    const compose = await new DockerComposeEnvironment(resolve(join(import.meta.dirname, '../')), ['compose-e2e.yml'])
        .withBuild()
        .withWaitStrategy("frontend-1", Wait.forHttp("/", 80).forStatusCode(200))
        .withWaitStrategy('user-service-1', Wait.forHealthCheck())
        .up()

    process.env.FRONTEND_PORT = compose.getContainer('frontend-1').getMappedPort(80).toString()

    return () => compose.down()
}