package applyextra.core.context;

/**
 * This class provides the RIAF context
 */
public class RIAFContextProviderImpl implements RIAFContextProvider {

	/**
	 * Gets the RIAF context
	 * RIAFContext is created by the {@link RIAFRequestContextHandlerInterceptor} on local thread.
	 * 
	 * This means that RIAFContext can not be injected directly. This provider can be used
	 * to get the {@link RIAFContext} after it has been created on the local thread.
	 * @return the RIAF context
	 */
	@Override
	public RIAFContext getRiafContext() {
		return RequestContext.getContext();
	}
}