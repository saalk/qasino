package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.constant.CorrelationType;
import applyextra.commons.model.database.entity.CorrelationEntity;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.CorrelationEntityUtil;
import applyextra.operations.dto.CramDTO;
import applyextra.operations.dto.CramStatus;
import nl.ing.riaf.core.util.JNDIUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;


/**
 * Common Move to Prepare the CRAMDTO.
 * Below properties need to be set
 * 1. param/smsText
 * 2. param/authSubject
 * 3. param/authText
 * 4. param/Origin
 * 5. param/mdmCode
 * 6. param/authSubject
 */
@Component
@Slf4j
@Lazy
public class PopulateCRAMDataAction implements Action<PopulateCRAMDataAction.PopulateCRAMDataActionDTO, EventOutput> {

    @Resource
    private JNDIUtil util;

    @Override
    public EventOutput perform(PopulateCRAMDataActionDTO dto) {
        final CramDTO cramDTO = new CramDTO();
        log.debug("Populating the CRAM DTO talking the values from the property files");
        cramDTO.setCramId(determineId(dto.getCreditcardRequest().getCorrelations()));
        cramDTO.setSmsText(getUnescapedString(util.getJndiValue("param/smsText")));
        cramDTO.setAuthSubject(getUnescapedString(util.getJndiValue("param/authSubject")));
        cramDTO.setAuthText(getUnescapedString(util.getJndiValue("param/authText")));
        cramDTO.setOrigin(getUnescapedString(util.getJndiValue("param/origin")));
        //mdmCode is the code that CRAM gives us Eg: 9058 for ApplyExtraCard
        cramDTO.setMdmCode(getUnescapedString(util.getJndiValue("param/mdmCode")));
        cramDTO.setMdmName(getUnescapedString(util.getJndiValue("param/mdmName")));
        cramDTO.setNotificationQueue(getUnescapedString(util.getJndiValue("param/notificationQueue")));
        cramDTO.setDescription(getUnescapedString(util.getJndiValue("param/processDescription")));
        cramDTO.setExpirationDate(DateTime.now().plusDays(1).toDate());
        cramDTO.setRiskLevel(dto.getRiskLevel());
        cramDTO.setPartyId(dto.getCustomerId());
        cramDTO.setStatus(dto.getCramStatus());
        cramDTO.setRequestId(dto.getRequestId());

        log.debug("Setting the flow dto with the populated cram dto");
        dto.setCramDTO(cramDTO);
        return EventOutput.success();
    }

    private String getUnescapedString(String value) {
        return StringEscapeUtils.unescapeHtml4(value);
    }

    //Private method to determine the cram id based on the correlations
    private String determineId( final Collection<CorrelationEntity> correlations) {
        CorrelationEntity correlationEntity = CorrelationEntityUtil.getCorrelationByType(correlations, CorrelationType.CRAM_ID);
        if (correlationEntity != null) {
            return correlationEntity.getExternalReference();
        }
        return null;
    }

    public interface PopulateCRAMDataActionDTO {

        CreditCardRequestEntity getCreditcardRequest();
        String getCustomerId();
        CramDTO.RiskLevel getRiskLevel();
        void setCramDTO(CramDTO cramDTO);
        CramStatus getCramStatus();
        String getRequestId();

    }
}
