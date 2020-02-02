package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.audit.AuditDelegate;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Component
@Slf4j
@Lazy
public class SelectCardEvent extends AbstractEvent {

    @Resource
    private ListCardsEvent listCardsEvent;
    @Resource
    private GetPartyInfoEvent getPartyInfoEvent;
    @Resource
    private AuditDelegate auditDelegate;

    @Override
    public EventOutput execution(final Object... eventInput) {
        SelectCardEventDTO flowDTO = (SelectCardEventDTO) eventInput[0];

        listCardsEvent.execution(flowDTO);      // collects cards related data
        flowDTO.setContextByCreditCard();       // fills the request context with credit playingcard data

        getPartyInfoEvent.execution(flowDTO);   // collects party related data
        flowDTO.setContextByAgreement();        // fills the request context with agreement data

        if (!flowDTO.isListCardResult()) {
            auditDelegate.fireSecurityEvent("PlayingCard id not part of user portfolio.");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }



        return EventOutput.success();
    }

    public interface SelectCardEventDTO extends ListCardsEvent.ListCardsEventDTO, GetPartyInfoEvent.GetPartyInfoEventDTO {
        boolean isListCardResult();
        void setContextByCreditCard();
        void setContextByAgreement();
    }
}
