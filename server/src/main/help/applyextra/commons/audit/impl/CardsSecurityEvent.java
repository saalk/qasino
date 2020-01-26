package applyextra.commons.audit.impl;

import lombok.EqualsAndHashCode;
import nl.ing.riaf.core.context.RIAFContext;
import nl.ing.riaf.core.event.audit.AbstractAuditEvent;
import nl.ing.riaf.core.event.audit.EventField;

import java.util.LinkedList;
import java.util.List;

/**
 * Event in case a security violation has been occurred.
 */
@EqualsAndHashCode(callSuper = false)
public class CardsSecurityEvent extends AbstractAuditEvent {

    private final String details;

    /**
     * Constructor of the audit event
     *
     * @param context RIAF context used to retrieve different properties of the request
     * @param details The details to be used
     */
    public CardsSecurityEvent(final RIAFContext context, final String details) {
        super(context);
        this.details = details;

        this.setSeverity(Severity.CRITICAL);
    }

    /**
     * Returns a list of specific fields
     *
     * @return list of fields
     */
    @Override
    public List<EventField> getSpecificFields() {
        final List<EventField> fields = new LinkedList<>();
        fields.add(new EventField("details", details));
        return fields;
    }
}
