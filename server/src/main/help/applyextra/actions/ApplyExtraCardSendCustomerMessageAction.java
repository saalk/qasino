package applyextra.actions;


import applyextra.applyextracard.model.ApplyExtraCardDTO;
import applyextra.api.sendcustomermessage.global.action.AbstractSendCustomerMessageAction;
import applyextra.api.sendcustomermessage.global.domain.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Lazy
public class ApplyExtraCardSendCustomerMessageAction extends AbstractSendCustomerMessageAction<ApplyExtraCardDTO> {

    private static final String SCM_TEMPLATE_ID = "A-CCRPC01";
    public static final Integer CC_ARRANGEMENT_KEY_TYPE = 63;
    private static final String ARRANGEMENT_TYPE = "arrangement";
    private static final String ARRANGEMENT_ROLES = "arrangement-roles";
    private static final String HOLDER_ROLE = "11";
    private static final String MEMBER_CHARACTER = "M";

    @Override
    public SendCustomerMessageBusinessRequest prePerform(ApplyExtraCardDTO applyExtraCardDTO) {

        Identifiers identifiers = Identifiers.builder()
                .messageId(UUID.fromString(applyExtraCardDTO.getCreditcardRequest().getId()))
                .build();

        MetaData metaData = MetaData.builder()
                .requestDate(LocalDateTime.now())
                .build();

        Subject subject = Subject.builder()
                .type(ARRANGEMENT_TYPE)
                .keyType(CC_ARRANGEMENT_KEY_TYPE)
                .keyValue(MEMBER_CHARACTER + applyExtraCardDTO.getSiaAccountNumber())
                .build();

        ContactPoint contactPoint = ContactPoint.builder()
                .type(ARRANGEMENT_ROLES)
                .roles(new String[]{HOLDER_ROLE})
                .build();

        Channels channels = Channels.builder()
                .primary(Destination.builder()
                        .type("inbox")
                        .templateName(SCM_TEMPLATE_ID)
                        .shouldAlert(false)
                        .output("A-CCR_OutputProfile")
                        .build())
                .secondary(Destination.builder()
                        .type("paper")
                        .templateName(SCM_TEMPLATE_ID)
                        .shouldAlert(true)
                        .output("A-CCR_OutputProfile")
                        .build())
                .ignoreErrors(false)
                .build();

        ArrayList<Channels> channelList = new ArrayList<>();
        channelList.add(channels);

        Map<String, Object> documentData = new LinkedHashMap<>();

        documentData.put("CreditLimitAmount", applyExtraCardDTO.getCreditcardRequest().getAccount().getAccountStatus().getCreditLimit());
        documentData.put("ProductName", applyExtraCardDTO.getCardType().name());
        documentData.put("ReferencedAccountNumber", applyExtraCardDTO.getIban());

        Payload payload = Payload.builder()
                .documentData(documentData)
                .build();

        return SendCustomerMessageBusinessRequest.builder()
                .subject(subject)
                .identifiers(identifiers)
                .contactPoint(contactPoint)
                .metaData(metaData)
                .channels(channelList)
                .payload(payload)
                .build();
    }

}
