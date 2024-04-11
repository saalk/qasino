package cloud.qasino.games.configuration;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;

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
