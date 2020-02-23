package applyextra.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.api.application.observers.CreditcardsRRBObserver;
import applyextra.api.application.observers.rrb.RRBResponse;
import applyextra.configuration.Constants;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.financialdata.CardType;
import nl.ing.riaf.core.util.JNDIUtil;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ApplyExtraCardRRBEvent extends AbstractEvent {

    @Resource
    private CreditcardsRRBObserver rrbObserver;

    @Resource
    private JNDIUtil jndiUtil;

    public static final String EXTRA_CARD_REQUEST_STEP = "40810";
    public static final String EXTRA_PLATINUM_CARD_REQUEST_STEP = "40820";

    public static final String RRB_USER_ID_KEY = "param/app/rrb/user-id";

    public static final String requestTypeCode = "00107";
    public static final String requestTypeReason = "00051";

    @Override
    protected EventOutput execution(final Object... eventOutput) {
        final ApplyExtraCardRRBEventDTO flowDTO = (ApplyExtraCardRRBEventDTO) eventOutput[0];

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("from_date", new DateTime());
        messageData.put("creditcard_number", flowDTO.getCreditCardNumber());
        messageData.put("beneficiary_id", flowDTO.getBeneficiaryId());
        messageData.put("request_type_code", requestTypeCode);
        messageData.put("request_reason_code", requestTypeReason);

        String requestStep = determineRequestStep(flowDTO.getCardType());
        if (requestStep == null) {
            return EventOutput.failure();
        }

        messageData.put("request_step", requestStep);
        messageData.put("user_id", jndiUtil.getJndiValue(RRB_USER_ID_KEY));
        final Map<String, Object> responseMap = rrbObserver.handleMessage(Constants.TOPIC, messageData);
        RRBResponse response = new RRBResponse(responseMap);
        handleResponse(response);
        return EventOutput.success();
    }

    private String determineRequestStep(CardType cardType){
        if (cardType == null){
            return null;
        }
        switch (cardType) {
            case Creditcard:
                return EXTRA_CARD_REQUEST_STEP;
            case Platinumcard:
                return EXTRA_PLATINUM_CARD_REQUEST_STEP;
            default:
                return null;
        }
    }

    private void handleResponse(final RRBResponse response) {
        switch (response.getResponseStatus()) {
            case OK:
                log.info("Updated RRB successfully.");
                break;
            case WARNING:
                handleError(response);
                break;
            case ERROR:
                handleError(response);
                break;
            default:
                throw new ActivityException("RRB", response.getResponseStatus().getCode(),
                        "Generic error during RRB update: " + response.getErrorText(), null);
        }
    }

    private void handleError(final RRBResponse response) {
        switch (response.getErrorReason()) {
            case OKE:
                log.info("Updated RRB successfully.");
                break;
            case NIET_AANW:
                throw new ActivityException("RRB", response.getErrorReason().getCode(),
                        "RRB record not found (could be already updated)!", null);
            default:
                throw new ActivityException("RRB", response.getErrorReason().getCode(),
                        "Generic error during RRB update. See RRB log " + response.getErrorLog()
                                + "; CICS-taks: " + response.getCicsTaskNo(),
                        null);
        }
    }

    public interface ApplyExtraCardRRBEventDTO {

        String getCreditCardNumber();

        String getBeneficiaryId();

        CardType getCardType();
    }

}
