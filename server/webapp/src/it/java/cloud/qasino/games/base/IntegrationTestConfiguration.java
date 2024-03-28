package cloud.qasino.games.base;

import lombok.SneakyThrows;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import javax.servlet.Filter;

@TestConfiguration
public class IntegrationTestConfiguration {

    @Bean
    public TestRestTemplate getTestRestTemplate(final SSLContext sslContext) {
        var socketFactory = new SSLConnectionSocketFactory(sslContext);
        var httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        var testRestTemplate = new TestRestTemplate();
        ((HttpComponentsClientHttpRequestFactory) testRestTemplate.getRestTemplate().getRequestFactory()).setHttpClient(httpClient);
        return testRestTemplate;
    }

}
