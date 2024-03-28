package cloud.qasino.games.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        // "merak-base" is the value on the OU in the certificate on identity.jks
)
@TestPropertySources({
        @TestPropertySource(value = "classpath:application-test.properties"),
        @TestPropertySource(value = "classpath:application-retry.properties"),
        @TestPropertySource(value = "classpath:application-business.properties")
})
@ContextConfiguration(
        initializers = {WireMockInitializer.class},
        classes = {IntegrationTestConfiguration.class}
)
@Slf4j
public abstract class AbstractBaseApplicationIT {

    @Resource
    protected TestRestTemplate testRestTemplate;

    @LocalServerPort
    protected int port;

    protected static final String HOST = "https://localhost";
    protected static final String ACCESS_TOKEN_HEADER = "local_access_token_profile";


    protected <T> ResponseEntity<String> callEndpoint(HttpMethod httpMethod, String endpoint, T requestPayload, String customerId) {
        final HttpHeaders headers = createHeaders();
        headers.add(ACCESS_TOKEN_HEADER, customerId);
        HttpEntity<T> httpEntity = requestPayload == null ? new HttpEntity<>(headers) : new HttpEntity<>(requestPayload, headers);

        return execute(
                httpMethod,
                endpoint,
                (HttpEntity<Object>) httpEntity,
                Collections.emptyMap());
    }

    // with params
    protected <T> ResponseEntity<String> callEndpoint(HttpMethod httpMethod, String endpoint, T requestPayload, String customerId, Map<String, String> params) {
        final HttpHeaders headers = createHeaders();
        headers.add(ACCESS_TOKEN_HEADER, customerId);
        HttpEntity<T> httpEntity = requestPayload == null ? new HttpEntity<>(headers) : new HttpEntity<>(requestPayload, headers);

        return execute(
                httpMethod,
                endpoint,
                (HttpEntity<Object>) httpEntity,
                params);
    }

    /**
     * Method for enriching any https call with default headers.
     * You can optionally add any necessary headers.
     *
     * @return default headers
     */
    private HttpHeaders createHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Host", "api.ing.com");
        return httpHeaders;
    }

    /**
     * Default method for executing the test calls for each IT developed within the context of this class.
     *
     * @param httpMethod       {@link HttpMethod}
     * @param fullPath         full fullPath of the endpoint
     * @param objectHttpEntity http entity object
     * @param urlParams        url parameters
     * @return ResponseEntity<String> response of the call
     */
    private ResponseEntity<String> execute(HttpMethod httpMethod, String fullPath, HttpEntity<Object> objectHttpEntity, Map<String, String> urlParams) {
        return testRestTemplate.exchange(
                HOST + ":" + port + fullPath,
                httpMethod,
                objectHttpEntity,
                String.class,
                urlParams);
    }

}





