package cloud.qasino.games;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , classes = GamesApplication.class
)
@TestPropertySources({
        @TestPropertySource(locations = "/application-ittest.properties"),
        @TestPropertySource(locations = "/application-business.properties")
})
@ContextConfiguration(classes = IntegrationTestConfiguration.class)
public abstract class AbstractBaseApplicationIT {

    private static final Logger log = LoggerFactory.getLogger(AbstractBaseApplicationIT.class);

    @Resource
    protected TestRestTemplate testRestTemplate;

    @LocalServerPort
    protected int port;

    protected static final String HOST = "http://localhost";
    protected static final String ACCESS_TOKEN_HEADER = "local_access_token_profile";
    protected static final String VISITOR = "visitor";

    // without params
    protected <T> ResponseEntity<String> callEndpoint(HttpMethod httpMethod, String endpoint, T requestPayload, long visitorId) {
        HttpHeaders headers = this.createHeaders();
        headers.add(VISITOR, String.valueOf(visitorId));
        HttpEntity<T> httpEntity = requestPayload == null ? new HttpEntity<>(headers) : new HttpEntity<>(requestPayload, headers);

        return this.execute(
                httpMethod,
                endpoint,
                (HttpEntity<Object>) httpEntity,
                Collections.emptyMap()); // no params
    }

    // with params
    protected <T> ResponseEntity<String> callEndpoint(HttpMethod httpMethod, String endpoint, T requestPayload, long visitorId, Map<String, String> params) {
        HttpHeaders headers = this.createHeaders();
        headers.add(VISITOR, String.valueOf(visitorId));
        HttpEntity<T> httpEntity = requestPayload == null ? new HttpEntity<>(headers) : new HttpEntity<>(requestPayload, headers);

        return this.execute(
                httpMethod,
                endpoint,
                (HttpEntity<Object>) httpEntity,
                params); // the params
    }

    /**
     * Method for enriching any https call with default headers.
     * You can optionally add any necessary headers.
     *
     * @return default headers
     */
    private HttpHeaders createHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Host", "cloud.qasino.games");
        return httpHeaders;
    }

    /**
     * Default method for executing the test httpcalls for each IT developed within the context of this class.
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





