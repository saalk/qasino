package applyextra.core.context;

/**
 * This class uses the ThreadLocal to expose a bean to every thread. Since every
 * request is handled by a separate thread, and e.g. the Log4J framework executes
 * in that same thread for it's logging, we can use this ThreadLocal context to
 * make some properties available anywhere within a request's thread.<br />
 * <br /><br />
 * For instance: the logged in customer: to be able to tie logging statements together,
 * we may log the logged in customer id and maybe a thread id (our some set correlation
 * id), so we receive understanding of the relation of different log statements from
 * different layers.
 * <br /><br />
 * Note: the request context is only supposed to be used for technical code (e.g. logging)
 * inside of RIAF. It is not intended for use outside of RIAF (is not intended to be called from
 * within actual projects). Therefore, the RequestContextBean is very inflexible. If e.g. the
 * party ID is needed somewhere in an application, it is part of the logic of that application.
 * All needed data part of the logic should be passed as parameters, instead of read from
 * this context.
 * 
 * @see RequestContextBean
 */
public final class RequestContext {

	  private static ThreadLocal<RIAFContext> localContext = new ThreadLocal<>();
	  
	  /** private constructor, since this is a utility class */
	  private RequestContext() { }
	  
	  /**
	   * @return return the context for this thread, or null if no context is set.
	   */
	  public static RIAFContext getContext() {
		  RIAFContext result;

		  final RIAFContext bean = localContext.get();
		  if (bean != null && !bean.isValid()) {
			  clearContext();
			  result = null;
		  } else {
			  result = bean;
		  }
		  
		  return result;
	  }
	  
	  /**
	   * Sets the request contextbean in the context
	   * @param bean request context bean
	   */
	  public static void setContext(final RIAFContext bean) {
		  localContext.set(bean);
	  }
	  
	  /**
	   * Clear the context for this thread. The context will be invalidated and removed.
	   */
	  public static void clearContext() {
		  if (localContext.get() != null) {
			  localContext.get().invalidate();
		  }
		  localContext.remove();
	  }
	  
	  /**
	   * If we have shared a context amongst threads (for PageControllor
	   * redirecting to widgets) we want to remove the context from the
	   * widgets thread, without invalidating it.
	   */
	  public static void removeLocalContext() {
		  localContext.remove();
	  }
}
