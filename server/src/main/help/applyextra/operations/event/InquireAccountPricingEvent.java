/**
 * 
 */
package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.operations.dto.SiaOrchestrationDTO;
import nl.ing.riaf.core.exception.RIAFRuntimeException;
import nl.ing.riaf.core.util.JNDIUtil;
import nl.ing.serviceclient.sia.common.configuration.ServiceWrapper;
import nl.ing.serviceclient.sia.inquireaccountpricing.dto.InquireAccountPricingBusinessRequest;
import nl.ing.serviceclient.sia.inquireaccountpricing.dto.InquireAccountPricingBusinessResponse;
import nl.ing.serviceclient.sia.inquireaccountpricing.jaxb.generated.MembershipFeeFMBResponseItem;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * class to call sia inquireaccountpricing service to get fee code and account number
 * 
 * @author Anand.Shukla Sep 21, 2016
 */
@Component
@Lazy
@Slf4j
public class InquireAccountPricingEvent extends AbstractEvent {

    public static final long DEFAULT_REQUEST_TIMEOUT = 5000;

    private static final String SERVICE_NAME_INQUIRE_ACCOUNT_PRICING = "inquireaccountpricing";

    private static final String INQUIRE_ACCOUNT_PRICING_SUCCESS = "00001";

    private long serviceTimeout = DEFAULT_REQUEST_TIMEOUT;

    @Resource
    private ServiceWrapper<InquireAccountPricingBusinessRequest, InquireAccountPricingBusinessResponse> inquireAccountPricingServiceClient;

    @Resource
    private JNDIUtil jndiUtil;

    @PostConstruct
    public void init() {
        serviceTimeout = jndiUtil.getJndiValueWithDefault("param/services/sia/timeout", DEFAULT_REQUEST_TIMEOUT);
    }

    @Override
    protected EventOutput execution(final Object... eventInput) {

        final InquireAccountPricingEventDTO flowDTO = (InquireAccountPricingEventDTO) eventInput[0];
        final String creditCardNumber = flowDTO.getSelectedCardNumber();
        final InquireAccountPricingBusinessRequest businessRequest = new InquireAccountPricingBusinessRequest();
        final InquireAccountPricingBusinessResponse businessResponse;

        businessRequest.setCreditCardNumber(creditCardNumber);

        try {
            businessResponse = inquireAccountPricingServiceClient.invoke(businessRequest, serviceTimeout);
        } catch (final RIAFRuntimeException e) {
            throw e; // will be caught and dealt with further up the chain
        } catch (final Exception e) {
            log.error("problem invoking execution", e);
            throw new ActivityException(SERVICE_NAME_INQUIRE_ACCOUNT_PRICING, "call to InquireAccountPricingEvent failed", e);
        }

        processResponse(businessResponse, flowDTO);

        return EventOutput.success();
    }

    private void processResponse(final InquireAccountPricingBusinessResponse response, final InquireAccountPricingEventDTO dto) {

        final SiaOrchestrationDTO siaOrchestrationDTO = dto.getSiaOrchestrationDTO();

        if (response.getInquireAccountPricingResponseItem() != null
                && INQUIRE_ACCOUNT_PRICING_SUCCESS.equals(response.getResponseMessage()
                        .getReturnCode())) {
            final List<MembershipFeeFMBResponseItem> membershipFeeFMBResponseItem = response.getInquireAccountPricingResponseItem().getMembershipFeeFMBResponseItem();
            if (membershipFeeFMBResponseItem != null && membershipFeeFMBResponseItem.get(0) !=null) {
                siaOrchestrationDTO.setFeeCode(membershipFeeFMBResponseItem.get(0)
                        .getFeeCode()); // set all the member fee codes into the dto
                siaOrchestrationDTO.setNextAnniversaryDate(new DateTime(membershipFeeFMBResponseItem.get(0).getFeeNextAnniversaryDate().toGregorianCalendar()));
                siaOrchestrationDTO.setMembershipFeeAmount(membershipFeeFMBResponseItem.get(0).getMembershipFeeAmount());
            }

        } else {
            final String returnCode = response.getResponseMessage()
                    .getReturnCode();
            log.error(String.format("SIA return code: %s, SIA return message: %s", returnCode, response.getResponseMessage()
                    .getReturnMessage()));
            throw new ActivityException(SERVICE_NAME_INQUIRE_ACCOUNT_PRICING,
                    "Error getting account information - ErrorCode: " + returnCode, null);
        }

        siaOrchestrationDTO.setInquireAccountPricingCode(response.getResponseMessage()
                .getReturnCode());
    }

    public interface InquireAccountPricingEventDTO {

        String getSelectedCardNumber();

        SiaOrchestrationDTO getSiaOrchestrationDTO();
    }

}