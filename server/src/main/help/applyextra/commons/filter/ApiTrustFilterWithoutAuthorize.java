package applyextra.commons.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;

@PreMatching
public class ApiTrustFilterWithoutAuthorize extends ApiTrustFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String uriPath = requestContext.getUriInfo().getPath();
        if (!"authorized".equals(uriPath) || !"/authorized".equals(uriPath)) {
            super.filter(requestContext);
        }
    }

}
