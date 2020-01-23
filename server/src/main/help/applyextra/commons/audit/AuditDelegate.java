package applyextra.commons.audit;

import applyextra.commons.audit.impl.CardsExchangeApiEvent;
import applyextra.commons.audit.impl.CardsSecurityEvent;
import applyextra.commons.audit.impl.WhichWay;

/**
 * I will define the methods of the audit delegate.
 */
public interface AuditDelegate {

    String WHICH_WAY_KEY = "whichWay";
    String HEADER_KEY = "header";

    /**
     * I will fire a {@link CardsExchangeApiEvent} with {@link WhichWay#IN}.
     *
     * @param path the path to be used
     * @param body the body to be used
     * @param header the header to be used
     */
    void fireExchangeInputApiEvent(String path, String body, String header);

    /**
     * I will fire a {@link CardsExchangeApiEvent} with {@link WhichWay#OUT}.
     *
     * @param path the path to be used
     * @param body the body to be used
     */
    void fireExchangeOutputApiEvent(String path, String body);

    /**
     * I will fire a {@link CardsSecurityEvent}.
     *
     * @param details the details to be used
     */
    void fireSecurityEvent(String details);

    /**
     * I will fire a subclass of {@link AbstractCardsAuditEvent}.
     *
     * @param event the event to fire
     */
    void fireGenericEvent(AbstractCardsAuditEvent event);

}
