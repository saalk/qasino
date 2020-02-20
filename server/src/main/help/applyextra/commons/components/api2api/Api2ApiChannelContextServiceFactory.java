package applyextra.commons.components.api2api;

import com.ing.api.toolkit.http.finagle.filter.StripJsonPrefixFilter;
import com.ing.api.toolkit.http.japi.ClientFactory;
import com.ing.api.toolkit.http.japi.RequestWithTrust;
import com.ing.api.toolkit.integration.discovery.Resolver;
import com.ing.api.toolkit.log.galm.GALMComponentManager;
import com.ing.api.toolkit.trust.context.ChannelContext;
import com.ing.api.trust.jwt.p2p.PeerToPeerTrustTokenBuilder;
import com.twitter.finagle.Service;
import com.twitter.finagle.http.Response;
import lombok.extern.slf4j.Slf4j;
import nl.ing.riaf.core.util.JNDIUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class Api2ApiChannelContextServiceFactory<RESPONSE> {
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

	   
	    
	    public Api2Api2ServiceChannelContext<RESPONSE> createApi2ApiService(final String httpMethod, final String applicationPath, final
	    	    String applicationName, final Class<RESPONSE> responseType, ChannelContext channelContext) {
	    	
	    	log.debug("Create the service with the applicationPath: " + applicationPath);
	    	
	    	final Service<RequestWithTrust, Response> client =
	    			ClientFactory.withTrust(applicationName,
	                        ClientFactory.createBasicHttpClient(applicationPath, serviceResolver));
	    	
	    	final Service<RequestWithTrust, Response> strippingClient =
	                new StripJsonPrefixFilter<RequestWithTrust>().andThen(client);
	    	
	    	 final Service<RequestWithTrust, RESPONSE> clientwithFilter =
	                 ClientFactory.withJSONMapperFilter(strippingClient, responseType);
	    	 
	    	 return new Api2Api2ServiceChannelContext<>(clientwithFilter,  httpMethod, applicationPath, channelContext);
	    }
	    
}
