package applyextra.commons.components.api2api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.api.toolkit.experimental.integration.patterns.ServicePattern;
import com.ing.api.toolkit.http.filter.Peer2PeerTrustFilter;
import com.ing.api.toolkit.integration.discovery.Resolver;
import com.ing.api.toolkit.log.galm.GALMComponentManager;
import com.ing.api.trust.jwt.p2p.PeerToPeerTrustTokenBuilder;
import com.twitter.finagle.Filter;
import com.twitter.finagle.Service;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;
import com.twitter.util.Duration;
import lombok.extern.slf4j.Slf4j;
import nl.ing.riaf.core.util.JNDIUtil;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.ing.api.toolkit.experimental.integration.patterns.http.DefaultJsonHttpServicePattern.apply;
import static com.ing.api.toolkit.finagle.filter.JSONMapperFilter.create;
import static com.ing.api.toolkit.http.japi.ClientFactory.createBasicHttpClient;

/**
 * Created by CL94WQ on 6-3-2017.
 */
@Slf4j
@Component
public class Api2ApiServiceFactory<REQUEST, RESPONSE> {

    protected static final String SERVICE_TIMEOUT_PARAM = "param/api-2-api/service-timeout";
    protected static final String TRANSPORT_TIMEOUT_PARAM = "param/api-2-api/transport-timeout";

    @Resource
    private PeerToPeerTrustTokenBuilder tokenBuilder;

    @Resource
    private GALMComponentManager galmComponentManager;

    @Resource
    private Resolver serviceResolver;

    @Resource
    private JNDIUtil jndiUtil;

    public Api2ApiService<REQUEST, RESPONSE> createApi2ApiService(final HttpMethod httpMethod, final String applicationPath, final
    String applicationName, final Class<RESPONSE> responseType) {
        log.debug("Create the service with the applicationPath: " + applicationPath);

        final Peer2PeerTrustFilter peer2PeerTrustFilter = Peer2PeerTrustFilter.create(tokenBuilder, applicationName);
        final ServicePattern<Request, RESPONSE, Request, Response> servicePattern = servicePattern(applicationName);
        final Filter<Request, RESPONSE, Request, Response> transformer = create(responseType, new ObjectMapper());
        final Service<Request, Response> client = peer2PeerTrustFilter
                .andThen(createBasicHttpClient(applicationPath, serviceResolver));

        Service<Request, RESPONSE> service = servicePattern.apply(client, transformer);
        return new Api2ApiService<>(service, httpMethod, applicationPath);
    }


    private ServicePattern<Request, RESPONSE, Request, Response> servicePattern(final String applicationName) {
        return apply(galmComponentManager, applicationName,
                Duration.fromMilliseconds(jndiUtil.getJndiValueWithDefault(SERVICE_TIMEOUT_PARAM, 5000)),
                Duration.fromMilliseconds(jndiUtil.getJndiValueWithDefault(TRANSPORT_TIMEOUT_PARAM, 3000)));
    }
}
