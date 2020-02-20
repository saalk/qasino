package applyextra.commons.audit.impl;

import applyextra.commons.audit.AbstractCardsAuditEvent;
import applyextra.commons.audit.AuditDelegate;
import applyextra.commons.util.ConstantsUtil;
import nl.ing.riaf.core.event.ApplicationEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Praveena Biyyam on 18-7-2018
 * @author Praveena Biyyam on 9-7-2018
 */
@Component
public class AuditDelegateHelper {

    @Resource
    private AuditDelegate auditDelegate;

    public void logMessage(final String id, final String message, final String messageId){
        auditDelegate.fireGenericEvent(new AbstractCardsAuditEvent(ApplicationEvent.Severity.LOW, WhichWay.IN) {
            @Override
            public void getSpecificFields(Map<String, Object> fields) {
                fields.put(ConstantsUtil.TRACE_ID, messageId);
                fields.put(ConstantsUtil.IDENTIFIER, id);
                fields.put(ConstantsUtil.MESSAGE, message);
            }
        });
    }

    public void logMessage(final String id, final Map<String, Object> message, final String messageId){
        auditDelegate.fireGenericEvent(new AbstractCardsAuditEvent(ApplicationEvent.Severity.LOW, WhichWay.IN) {
            @Override
            public void getSpecificFields(Map<String, Object> fields) {
                fields.put(ConstantsUtil.TRACE_ID, messageId);
                fields.put(ConstantsUtil.IDENTIFIER, id);
                fields.putAll(message);
            }
        });
    }

}
