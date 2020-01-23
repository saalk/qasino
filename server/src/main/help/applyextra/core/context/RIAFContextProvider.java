package applyextra.core.context;

/**
 * Interface for the RIAF context Provider
 */
public interface RIAFContextProvider {

	/**
	 * Gets the RIAF context
	 * RIAFContext is created by the {@link RIAFRequestContextHandlerFilter} on local thread.
	 * 
	 * This means that RIAFContext can not be injected directly. This provider can be used
	 * to get the {@link RIAFContext} after it has been created on the local thread.
	 * @return the RIAF context
	 */
	RIAFContext getRiafContext();
}
