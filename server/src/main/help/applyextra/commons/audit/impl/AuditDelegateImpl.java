package applyextra.commons.audit.impl;

import applyextra.commons.audit.AbstractCardsAuditEvent;
import applyextra.commons.audit.AuditDelegate;
import nl.ing.riaf.core.context.RIAFContext;
import nl.ing.riaf.core.context.RIAFContextProvider;
import nl.ing.riaf.core.event.ApplicationEventManager;
import nl.ing.riaf.core.event.audit.AbstractAuditEvent;
import nl.ing.riaf.core.event.audit.EventField;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class AuditDelegateImpl implements AuditDelegate {

	private static final String KEEPALIVE_PATH = "/keepalive";

	/**
	 * RIAF class to fire an move for the audit logging
	 */
	@Resource
	private ApplicationEventManager applicationEventManager;

	/**
	 * The RIAF object to get the RIAF context
	 */
	@Resource
	private RIAFContextProvider riafContextProvider;

	@Override
	public void fireExchangeInputApiEvent(final String path, final String body, final String header) {
		if (notAKeepAliveEvent(path)) {
			applicationEventManager.fireEvent(new CardsExchangeApiEvent(riafContextProvider.getRiafContext(), path, WhichWay.IN, body, header));
		}
	}

	/**
	 * No keep alive events in the audit trail as this is not relevant for auditing purpose.
	 *
	 * @param path
	 * @return boolean
	 */
	private boolean notAKeepAliveEvent(String path) {
		return !StringUtils.contains(path, KEEPALIVE_PATH);
	}

	@Override
	public void fireExchangeOutputApiEvent(final String path, final String body) {
		if (notAKeepAliveEvent(path)) {
			applicationEventManager.fireEvent(new CardsExchangeApiEvent(riafContextProvider.getRiafContext(), path, WhichWay.OUT, body));
		}
	}

	@Override
	public void fireSecurityEvent(final String details) {
		applicationEventManager.fireEvent(new CardsSecurityEvent(riafContextProvider.getRiafContext(), details));
	}

	@Override
	public void fireGenericEvent(final AbstractCardsAuditEvent event) {
		applicationEventManager.fireEvent(new GenericEventWrapper(riafContextProvider.getRiafContext(), event));
	}

	private class GenericEventWrapper extends AbstractAuditEvent {

		private AbstractCardsAuditEvent event;

		public GenericEventWrapper(final RIAFContext context, final AbstractCardsAuditEvent event) {
			super(context);
			this.event = event;
			this.setSeverity(event.getSeverity());
		}

		@Override
		public List<EventField> getSpecificFields() {
			final List<EventField> result = new LinkedList<>();

			// First find the move specific fields
			final Map<String, Object> fields = new HashMap<>();

			event.getSpecificFields(fields);
			for (Map.Entry<String, Object> entry : fields.entrySet()) {
				result.add(new EventField(entry.getKey(), String.valueOf(entry.getValue())));
			}

			// then add general fields
			result.add(new EventField(WHICH_WAY_KEY, event.getWhichWay().toString()));

			final String header = event.getCommaDelimitedHeaders();
			if (header != null) {
				result.add(new EventField(HEADER_KEY, header));
			}

			return result;
		}
	}

}
