package applyextra.commons.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import applyextra.commons.audit.impl.CardsExchangeApiEvent;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * I will trigger the {@link AuditDelegate} to fire the
 * {@link CardsExchangeApiEvent}.
 *
 * Pattern based this must be done by interceptors, like
 * - {@link javax.ws.rs.ext.ReaderInterceptor}
 * - {@link javax.ws.rs.ext.WriterInterceptor}
 * Instead the filters
 * - {@link ContainerRequestFilter}
 * - {@link ContainerResponseFilter}
 * are used which are triggered when there is no body available. This is relevant for GET.
 */
@Slf4j
public class AuditRequestResponseFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Autowired
    private AuditDelegate auditDelegate;

    private final static ObjectMapper mapper = new ObjectMapper();

    //For now we only add account. To be extended when we add more values to the header.
    private final static String[] HEADERS_KEYS_TO_AUDIT_LOG = {"account"};

    private final static String HEADER_SEPARATOR = ":";

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        log.debug(("Filtering request"));

        final String body = getRequestBodyAndPassRequestThrough(requestContext);

        //Getting the header key and values which are entered by the user.
        final String headerKeyValueAsCommaDelimitedString = getHeaderKeyValueAsCommaDelimitedString(requestContext);

        auditDelegate.fireExchangeInputApiEvent(getPath(requestContext), body, headerKeyValueAsCommaDelimitedString);
    }

    private String getPath(final ContainerRequestContext requestContext) {
        return requestContext.getUriInfo().getAbsolutePath().getPath();
    }

    private String getHeaderKeyValueAsCommaDelimitedString(final ContainerRequestContext requestContext) {

        final MultivaluedMap<String, String> headers = requestContext.getHeaders();
        final List<String> header = new ArrayList<>();

        for (final String headerKey : HEADERS_KEYS_TO_AUDIT_LOG) {
            final String headerValue = headers.getFirst(headerKey);
            if (StringUtils.hasText(headerValue)) {
                final StringBuilder sb = new StringBuilder();
                sb.append(headerKey);
                sb.append(HEADER_SEPARATOR);
                sb.append(headerValue);
                header.add(sb.toString());
            }
        }

        return StringUtils.collectionToCommaDelimitedString(header);
    }

    private String getRequestBodyAndPassRequestThrough(final ContainerRequestContext requestContext) throws IOException {

        String body = null;
        InputStream entityStream = null;
        try {
            entityStream = requestContext.getEntityStream();
            body = IOUtils.toString(entityStream);
        } finally {
            IOUtils.closeQuietly(entityStream);
        }
        final InputStream in = IOUtils.toInputStream(body);
        requestContext.setEntityStream(in);

        return body;
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
        log.debug("Filtering response");

        final Object entity = responseContext.getEntity();
        String jsonString = null;

        if (entity != null) {
            jsonString = mapper.writeValueAsString(entity);
        }

        auditDelegate.fireExchangeOutputApiEvent(getPath(requestContext), jsonString);
    }
}
