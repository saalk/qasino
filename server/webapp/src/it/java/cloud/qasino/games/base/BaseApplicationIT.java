package cloud.qasino.games.base;

import cloud.qasino.games.database.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;

import static cloud.qasino.games.configuration.Constants.BASE_PATH;
import static cloud.qasino.games.configuration.Constants.ENDPOINT_USERS;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {TestDatabaseAutoConfiguration.class})
@EnableAutoConfiguration(exclude = TestDatabaseAutoConfiguration.class)
@Slf4j
class BaseApplicationIT extends AbstractBaseApplicationIT {

    @Resource protected GameRepository gameRepository;
//    @Resource protected WireMockServer wireMockServer;
//    @Resource protected WiremockUtil wiremockUtil;

//    @BeforeAll
//    public void setUp() {
//        wiremockUtil.loadStubs(wireMockServer);
//    }

    <T> ResponseEntity<String> callEndpointQasino(HttpMethod httpMethod, String endpoint, T requestPayload, String customerId) {
        return this.callEndpoint(httpMethod, BASE_PATH + endpoint, requestPayload, customerId);
    }

    <T> ResponseEntity<String> callEndpointQasino(HttpMethod httpMethod, String endpoint, T requestPayload, String customerId, Map<String,String> params) {
        return this.callEndpoint(httpMethod, BASE_PATH + endpoint, requestPayload, customerId, params);
    }

    ResponseEntity<String> callUsersEndpoint(final String customerId,
                                                   final String requestId) {
        return this.callEndpoint(HttpMethod.PUT, BASE_PATH + ENDPOINT_USERS, "",customerId, Collections.singletonMap("REQUEST_ID", requestId));
    }

}