package applyextra.commons.filter;

import com.ing.api.toolkit.trust.context.*;
import com.ing.api.toolkit.trust.context.exception.InvalidContextException;
import com.ing.api.toolkit.trust.util.ContainerRequestContextToChannelContextSourceConverter;
import com.ing.api.trust.jwt.p2p.PeerToPeerTrustToken;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import applyextra.commons.audit.AuditDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;

@PreMatching
@Slf4j
public class ApiTrustFilter implements ContainerRequestFilter {

	@Autowired
	@Getter
	@Setter
	private ChannelContextFactory channelContextFactory;

	@Autowired
    private AuditDelegate auditDelegate;

	@Override
	public void filter(final ContainerRequestContext requestContext) {
		final String uriPath = requestContext.getUriInfo().getPath();
		if (!("keepalive".equals(uriPath) || "/keepalive".equals(uriPath))) {
			try {
				final PeerToPeerTrustToken peerToPeerTrustToken = PeerToPeerTrustTokenHelper.getPeerToPeerTrustToken(requestContext);
				if (peerToPeerTrustToken == null) {
					final ChannelContextSource channelContextSource = ContainerRequestContextToChannelContextSourceConverter
						.convert(requestContext);
					final ChannelContext channelContext = channelContextFactory.createContext(channelContextSource);
					ChannelContextPropertyHelper.setChannelContext(requestContext, channelContext);
				} else if (!peerToPeerTrustToken.isVerified()) {
                    handleResult(requestContext, uriPath, null);
				}
			} catch (InvalidContextException ice) {
                handleResult(requestContext, uriPath, ice);
			}
		}
	}

    private void handleResult(ContainerRequestContext requestContext, String uriPath, InvalidContextException ice) {
        log.error("Invalid Context", ice);
        auditDelegate.fireSecurityEvent("Unauthorized request for path " + uriPath + ", Returning http: 403");
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }
}