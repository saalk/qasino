package applyextra.commons.components.api2api;

import com.ing.api.toolkit.http.japi.RequestWithTrust;
import com.ing.api.toolkit.http.japi.RichHttpRequestBuilder;
import com.ing.api.toolkit.trust.context.ChannelContext;
import com.twitter.finagle.Service;
import com.twitter.util.Await;
import com.twitter.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Api2Api2ServiceChannelContext <RESPONSE>{
    private static final long TIMEOUT_IN_MILLIS = 5000;
    private final Service<RequestWithTrust, RESPONSE> service;
    private final String path;
    private final String httpMethod;
    private final ChannelContext channelContext;


    public Api2Api2ServiceChannelContext(Service<RequestWithTrust, RESPONSE> clientwithFilter, String httpMethod,
			String applicationPath, ChannelContext channelContext) {
    	this.service = clientwithFilter;
        this.httpMethod = httpMethod;
        this.path = applicationPath;
        this.channelContext = channelContext;
    }

	public RESPONSE call() throws Exception {
        
        final RequestWithTrust requestWithTrust = new RichHttpRequestBuilder()
                .withMethod(httpMethod)
                .withPath(path)
                .build(channelContext);
        return Await.result(service.apply(requestWithTrust), Duration.fromMilliseconds(TIMEOUT_IN_MILLIS));
    }
}
