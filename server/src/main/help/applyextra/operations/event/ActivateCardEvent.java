package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.commons.helper.GraphiteHelper;
import nl.ing.riaf.core.util.JNDIUtil;
import nl.ing.riaf.ix.IXActivity;
import nl.ing.riaf.ix.IXResponse;
import nl.ing.serviceclient.sia.activatecard2.dto.ActivateCard2BusinessRequest;
import nl.ing.serviceclient.sia.activatecard2.dto.ActivateCard2BusinessResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
@Slf4j
@Lazy
public class ActivateCardEvent extends AbstractEvent {
    private static final long DEFAULT_REQUEST_TIMEOUT = 5000;

    private static final String SERVICE_NAME = "Activate PlayingCard";
    private static final String ACTIVATECARD_CATEGORY = "sia.activatecard";

    private long serviceTimeout = DEFAULT_REQUEST_TIMEOUT;

    @Resource
    private GraphiteHelper graphiteHelper;
    @Resource
    private IXActivity <ActivateCard2BusinessRequest, ActivateCard2BusinessResponse> serviceClient;
    @Resource
    private JNDIUtil jndiUtil;

    @PostConstruct
    public void init() {
        serviceTimeout = jndiUtil.getJndiValueWithDefault("param/services/sia/timeout", DEFAULT_REQUEST_TIMEOUT);
    }

    @Override
    protected EventOutput execution(final Object... eventInput) {
        ActivateCardEventDTO flowDTO = (ActivateCardEventDTO) eventInput[0];
        final String cardNumber = flowDTO.getCreditCardNumber();

        final ActivateCard2BusinessRequest businessRequest = ActivateCard2BusinessRequest.builder()
                .cardNumber(cardNumber)
                .userId("API" + flowDTO.getApplicationId())
                .build();

        final ActivateCard2BusinessResponse businessResponse;

        final IXResponse<ActivateCard2BusinessRequest, ActivateCard2BusinessResponse> res = serviceClient.execute(businessRequest);
        if (res.isOk()) {
            businessResponse = res.getOk().getResponse();
        } else {
            throw new ActivityException(SERVICE_NAME, "Call to ActivateCard Failed. Response isOk == false.");
        }

        ActivateCardReturnCode responseCode = ActivateCardReturnCode.fromString(businessResponse.getReturnCode());

        switch (responseCode) {
            case ALREADY_ACTIVATED:
                graphiteHelper.customCounter(ACTIVATECARD_CATEGORY, "alreadyactive", 1);
                responseCode = ActivateCardReturnCode.ACTIVATE_SUCCESSFUL; // The customer has an activated playingcard after this call
                break;
            case CARD_NOT_FOUND:
                graphiteHelper.customCounter(ACTIVATECARD_CATEGORY, "cardnotfound", 1);
                break;
            case ACTIVATE_TOO_CLOSE_TO_EXPIRATION_DATE:
                graphiteHelper.customCounter(ACTIVATECARD_CATEGORY, "tooclosetoexpirydate", 1);
                break;
        }

        if (responseCode != ActivateCardReturnCode.ACTIVATE_SUCCESSFUL) {
            log.error(new StringBuilder()
                    .append("Error calling ")
                    .append(SERVICE_NAME).append(" : ")
                    .append(businessRequest.getCardNumber())
                    .append(", Return Code: ").append(businessResponse.getReturnCode())
                    .toString());
            throw new ActivityException(SERVICE_NAME, "Call to activate playingcard failed.");
        }

        return EventOutput.success();
    }

    public interface ActivateCardEventDTO {
        String getCreditCardNumber();
        String getApplicationId();
    }

    public enum ActivateCardReturnCode {

        ACTIVATE_SUCCESSFUL(0, 1, 4, 4923, 10000),
        ALREADY_ACTIVATED(4336, 7574),
        CARD_NOT_FOUND(2009, 5788, 7671),
        ACTIVATE_TOO_CLOSE_TO_EXPIRATION_DATE(6678),
        NO_ACTIVATION_PERMITTED(4198, 4199),
        METHOD_NOT_VALID(1327),
        MISSING_DATA(99999),
        UNKNOWN(-1);

        private final int[] siaErrorCodes;

        ActivateCardReturnCode(final int... siaErrorCodes) {
            this.siaErrorCodes = siaErrorCodes;
        }

        public static ActivateCardReturnCode fromString(final String errorCode) {
            return fromInteger(Integer.parseInt(errorCode));
        }

        public static ActivateCardReturnCode fromInteger(final int errorCode) {
            for (ActivateCardReturnCode code : ActivateCardReturnCode.values()) {
                for(int siaCode : code.siaErrorCodes) {
                    if (siaCode == errorCode) {
                        return code;
                    }
                }
            }
            return UNKNOWN;
        }

    }

}
