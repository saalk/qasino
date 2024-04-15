package cloud.qasino.games;

import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

// separate test configuration class
@TestConfiguration
public class IntegrationTestConfiguration {

    @Bean
    public TestRestTemplate getTestRestTemplate() {
//        var socketFactory = new SSLConnectionSocketFactory(sslContext);
//        var httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        var httpClient = HttpClients.createDefault();
        var testRestTemplate = new TestRestTemplate();
        ((HttpComponentsClientHttpRequestFactory) testRestTemplate.getRestTemplate().getRequestFactory()).setHttpClient(httpClient);
        return testRestTemplate;
    }

}
