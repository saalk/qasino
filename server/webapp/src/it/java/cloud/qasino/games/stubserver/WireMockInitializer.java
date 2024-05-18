package cloud.qasino.games.stubserver;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * Can start the Wiremock instance when the Application initialized. To use this, annotate the test class with:
 * like so: @ContextConfiguration(initializers = { WireMockInitializer.class }).
 */
@Slf4j
public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    // Properties takes for SslProperties. The initializers cannot load a propertyConfigurationClass, so we have to query for them individually.
    private static final String SERVER_SSL_KEY_STORE_PASSWORD = "server.ssl.key-store-password";
    private static final String SERVER_SSL_TRUST_STORE_PASSWORD = "server.ssl.trust-store-password";

    @SneakyThrows
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();

        var wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicHttpsPort()
                .extensions(new ResponseTemplateTransformer(true))
//                .keystorePath(ResourceUtils.getFile("classpath:identity.jks").getAbsolutePath())
//                .keystorePassword(environment.getProperty(SERVER_SSL_KEY_STORE_PASSWORD))
//                .keyManagerPassword(environment.getProperty(SERVER_SSL_KEY_STORE_PASSWORD))
//                .trustStorePath(ResourceUtils.getFile("classpath:trust.jks").getAbsolutePath())
//                .trustStorePassword(environment.getProperty(SERVER_SSL_TRUST_STORE_PASSWORD))
                .asynchronousResponseEnabled(true)
                .asynchronousResponseThreads(10)
                .usingFilesUnderClasspath("src/it/resources")
        );
        wireMockServer.start();

        configurableApplicationContext
                .getBeanFactory()
                .registerSingleton("wireMockServer", wireMockServer);

        configurableApplicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent) {
                log.warn("!!! Shutting down wiremock server !!! ... not happy with when this is happening, do some investigations why");
                wireMockServer.stop();
            }
        });
        Map<String, String> wiremockProps = new HashMap<>() {{
            put("custom.wiremock.port", String.valueOf(wireMockServer.httpsPort()));
        }};
        TestPropertyValues
                .of(wiremockProps)
                .applyTo(configurableApplicationContext);
    }
}
