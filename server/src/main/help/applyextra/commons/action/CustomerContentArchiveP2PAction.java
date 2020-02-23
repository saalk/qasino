package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.api.exception.ResourceException;
import applyextra.commons.audit.AbstractCardsAuditEvent;
import applyextra.commons.audit.AuditDelegate;
import applyextra.commons.audit.impl.WhichWay;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.customercontent.CustomerContentResourceClient;
import applyextra.customercontent.model.DocumentVO;
import applyextra.customercontent.value.CustomerContentBusinessRequest;
import applyextra.customercontent.value.CustomerContentBusinessResponse;
import nl.ing.riaf.core.event.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import java.util.Map;

@Component
@Lazy
@Slf4j
public class CustomerContentArchiveP2PAction implements Action<CustomerContentArchiveP2PAction.CustomerContentArchiveDTO, EventOutput> {

    @Resource
    private CustomerContentResourceClient resourceClient;

    @Resource
    AuditDelegate auditDelegate;

    @Override
    public EventOutput perform(CustomerContentArchiveDTO dto) {
        DocumentVO documentVO = dto.getDocumentVO();
        CustomerContentBusinessRequest businessRequest = new CustomerContentBusinessRequest();
        businessRequest.setDocumentVO(documentVO);
        try {
            logMessage(documentVO.getDocumentId(), documentVO.getMessage(), WhichWay.OUT);
            CustomerContentBusinessResponse response = resourceClient.execute(businessRequest);
            logMessage(response.getDocumentId(), response.getMessage(), WhichWay.IN);
        } catch (ResourceException e) {
            log.error("Unexpected exception while calling gCustomerContentApi : " + e.getMessage());
            throw new WebApplicationException(e, javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR);
        }
        return EventOutput.success();
    }

    public interface CustomerContentArchiveDTO {
        DocumentVO getDocumentVO();
    }


    private final void logMessage(final String documentId, final String message, final WhichWay direction) {
        auditDelegate.fireGenericEvent(new AbstractCardsAuditEvent(ApplicationEvent.Severity.LOW, direction) {
            @Override
            public void getSpecificFields(final Map<String, Object> map) {
                map.put("DocumentId", documentId);
                map.put("Message", message);
            }
        });
    }

}
