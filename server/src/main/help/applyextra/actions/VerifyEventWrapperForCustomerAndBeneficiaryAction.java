package applyextra.actions;

import lombok.extern.slf4j.Slf4j;
import applyextra.configuration.Constants;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.operations.dto.RejectReasonDTO;
import applyextra.operations.event.VerifyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

/**
 *  Rules for the VerifyEvent:      Requestor       Beneficiary
 *      Not WWFT compliant          NO              NO
 *      FEC risicoclassificatie     NO              NO
 *      Arrears                     NO
 *      EVA                         NO              NO
 *      Fraude                      NO              NO
 *      RR-kenmerk                  NO              NO
 *      Deceased                    NO              NO
 *      Not Holder                  NO
 */
@Lazy
@Slf4j
@Component
public class VerifyEventWrapperForCustomerAndBeneficiaryAction implements Action<VerifyEventWrapperForCustomerAndBeneficiaryAction.VerifyEventWrapperForBeneficiaryActionDTO, EventOutput.Result> {

    @Resource
    private VerifyEvent verifyEvent;


    @Override
    public EventOutput.Result perform(VerifyEventWrapperForBeneficiaryActionDTO dto) {
        final VerifyEvent.VerifyEventDTO dtoForCustomer = mapValuesToVerifyDto(dto,
                Constants.VERIFYEVENT_VERIFICATION_CODE_CUSTOMER,
                dto.getCustomerId());
        final EventOutput.Result resultVerifyCustomer = verifyEvent.fireEvent(dtoForCustomer).getResult();
        if (!EventOutput.Result.SUCCESS.equals(resultVerifyCustomer)) {
            log.warn("Verify Turn Failed for requestId: " + dto.getRequestId() +" while checking for the customer, " +
                    "Result = " + resultVerifyCustomer);
            return EventOutput.Result.FAILURE;
        }

        final VerifyEvent.VerifyEventDTO dtoForBeneficiary = mapValuesToVerifyDto(dto,
                Constants.VERIFYEVENT_VERIFICATION_CODE_BENEFICIARY,
                dto.getBeneficiaryId());
        final EventOutput.Result resultVerifyBeneficiary = verifyEvent.fireEvent(dtoForBeneficiary).getResult();
        if (!EventOutput.Result.SUCCESS.equals(resultVerifyBeneficiary)) {
            log.warn("Verify Turn Failed for requestId: " + dto.getRequestId() + " while checking for the beneficiary, " +
                    "Result = " + resultVerifyBeneficiary);
            return EventOutput.Result.FAILURE;
        }

        return EventOutput.Result.SUCCESS;
    }


    private VerifyEvent.VerifyEventDTO mapValuesToVerifyDto(VerifyEventWrapperForBeneficiaryActionDTO dto,
                                                            BigInteger verificationCode,
                                                            String forPartyId) {
        return new VerifyEvent.VerifyEventDTO() {
            @Override public String getArrangementId() {
                // Null should be returned according to VerifyOperation in case of beneficiary.
                return isAllowed(verificationCode) ? dto.getArrangementId() : null;
            }
            @Override public BigInteger getArrangementType() {
                // Null should be returned according to VerifyOperation in case of beneficiary.
                return isAllowed(verificationCode) ? dto.getArrangementType() : null;
            }
            @Override public BigInteger getVerificationCode() { return verificationCode; }
            @Override public String getRequestId() { return dto.getRequestId(); }
            @Override public String getCustomerId() { return forPartyId; }
            @Override public List<RejectReasonDTO> getRejectReason() { return dto.getRejectReason(); }
            @Override public String getVerificationStatus() { return dto.getVerificationStatus(); }
            @Override public void setVerificationStatus(String verificationStatus) {
                dto.setVerificationStatus(verificationStatus);
            }
        };
    }

    public interface VerifyEventWrapperForBeneficiaryActionDTO extends VerifyEvent.VerifyEventDTO {
        String getBeneficiaryId();
    }

    private boolean isAllowed(BigInteger verificationCode) {
        return verificationCode.equals(Constants.VERIFYEVENT_VERIFICATION_CODE_CUSTOMER);
    }
}
