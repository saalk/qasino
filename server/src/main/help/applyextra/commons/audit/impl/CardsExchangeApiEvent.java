package applyextra.commons.audit.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import nl.ing.riaf.core.context.RIAFContext;
import nl.ing.riaf.core.event.audit.AbstractAuditEvent;
import nl.ing.riaf.core.event.audit.EventField;
import nl.ing.riaf.presentation.rest.JsonWebException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;


/**
 * Turn for input and output of API calls.
 * This move will hold data like: path, {@link WhichWay}, body and sometimes the header.
 */
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class CardsExchangeApiEvent extends AbstractAuditEvent {

    private final String body;
    private final String header;
    private final String path;
    private final WhichWay whichWay;

    /**
     * Constructor of the audit move
     *
     * @param context RIAF context used to retrieve different properties of the request
     * @param path The path to be used
     * @param whichWay The {@link WhichWay} to be used
     * @param body The body to be used
     * @param header The header to be used
     */
    public CardsExchangeApiEvent(final RIAFContext context, final String path, final WhichWay whichWay, final String body, final String header) {
        super(context);
        this.path = path;
        this.whichWay = whichWay;
        this.body = body;
        this.header = header;

        this.setSeverity(Severity.LOW);
    }

    /**
     * Constructor of the audit move
     *
     * @param context RIAF context used to retrieve different properties of the request
     * @param path The path to be used
     * @param whichWay The {@link WhichWay} to be used
     * @param body The body to be used
     */
    public CardsExchangeApiEvent(final RIAFContext context, final String path, final WhichWay whichWay, final String body) {
        this(context, path, whichWay, body, null);
    }

    /**
     * Returns a list of specific fields
     *
     * @return list of fields
     */
    @Override
    public List<EventField> getSpecificFields() {
        final List<EventField> fields = super.getSpecificFields();

        fields.add(new EventField("whichWay", whichWay.toString()));

        if (StringUtils.hasText(header)) {
            fields.add(new EventField("header", header));
        }

        fields.add(new EventField("path", path));

        //some api exchange moves are only a GET without any other input than the customer id
        if (StringUtils.hasText(body)) {

            final ObjectMapper mapper = new ObjectMapper();
            try {
                final Object result = mapper.readValue(body, Object.class);
                final String notFormattedJson = mapper.writeValueAsString(result);
                fields.add(new EventField("json", notFormattedJson));
            } catch (IOException e) {
                log.warn("Exception occurred", e);
                throw new JsonWebException(e);
            }
        }
        return fields;
    }
}

