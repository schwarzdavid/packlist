package app.schwarzdavid.packlist.list.service.fixture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.client.RestTestClient;

@ListServiceIntegrationTest
public abstract class ApiTest {
    @Autowired
    protected RestTestClient client;
    @Value("${USER_SERVICE_HOSTNAME}")
    private String userServiceHostname;

    protected String getJwt() {
        return client.get().uri("%s/test-user".formatted(userServiceHostname)).exchange()
                .expectStatus().isOk()
                .returnResult(String.class).getResponseBody();
    }
}
