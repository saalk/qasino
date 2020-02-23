package applyextra.operations.filter;

import com.ing.api.toolkit.trust.context.ChannelContext;
import com.ing.api.toolkit.trust.context.ChannelContextPropertyHelper;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Set;

@Slf4j
@Priority(Priorities.AUTHORIZATION)
public class AllowedSubChannelsRequestFilter implements ContainerRequestFilter {
    private final Set<String> allowedSubChannels;

    /**
     * Initiate filters with allowed subchannels
     * @param allowedSubChannels
     */
    public AllowedSubChannelsRequestFilter(final Set<String> allowedSubChannels) {
        this.allowedSubChannels = allowedSubChannels;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        final ChannelContext channelContext = ChannelContextPropertyHelper.getChannelContext(containerRequestContext);

        if (!isAllowed(channelContext)) {
            log.warn("User attempted to access {}",containerRequestContext.getUriInfo().getAbsolutePath().toASCIIString());

            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private Boolean isAllowed(final ChannelContext channelContext) {

        //default not allowed
        boolean isAllowed = false;

        //Check if channelType or subChannelType match the allowedSubChannels
        if (channelContext != null && channelContext.getContextSession() != null) {
            String channelSubType = null;
            String channelType = null;
            if (channelContext.getContextSession().getChannelType().isPresent()) {
                channelType = channelContext.getContextSession().getChannelType().get();
            }
            if (channelContext.getContextSession().getChannelSubtype().isPresent()) {
                channelSubType = channelContext.getContextSession().getChannelSubtype().get();
            }
            isAllowed = (channelType!= null && allowedSubChannels.contains(channelType)
                || channelSubType != null && allowedSubChannels.contains(channelSubType));
        }

        return isAllowed;
    }
}
