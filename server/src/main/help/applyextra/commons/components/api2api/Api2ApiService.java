package applyextra.commons.components.api2api;

import com.ing.api.toolkit.http.japi.RichHttpRequestBuilder;
import com.twitter.finagle.Service;
import com.twitter.finagle.http.Request;
import com.twitter.util.Await;
import com.twitter.util.Duration;
import org.jboss.netty.handler.codec.http.HttpMethod;

/**
 * Created by CL94WQ on 16-3-2017.
 */
public class Api2ApiService<REQUEST, RESPONSE> {

    //TODO make configurable
    private static final long TIMEOUT_IN_MILLIS = 5000;

    private final Service<Request, RESPONSE> service;
    private final String path;
    private final HttpMethod httpMethod;

    public Api2ApiService(final Service<Request, RESPONSE> service, HttpMethod httpMethod, String path) {
        this.service = service;
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public RESPONSE call(REQUEST request) throws Exception {
        Request httpRequest = new RichHttpRequestBuilder().withMethod(httpMethod)
                .withJsonContent(request)
                .withPath(path)
                .build();
        return Await.result(service.apply(httpRequest), Duration.fromMilliseconds(TIMEOUT_IN_MILLIS));
    }

}
