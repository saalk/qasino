package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.commons.event.EventOutput.Result;
import applyextra.operations.dto.RejectReasonDTO;
import nl.ing.riaf.core.exception.IntegrationException;
import nl.ing.serviceclient.verifycreditcardoperation.dto.RejectReasonType;
import nl.ing.serviceclient.verifycreditcardoperation.dto.VerifyCreditCardRequest;
import nl.ing.serviceclient.verifycreditcardoperation.service.VerifyCreditCardOperationRequest;
import nl.ing.serviceclient.verifycreditcardoperation.service.VerifyCreditCardOperationResponse;
import nl.ing.serviceclient.verifycreditcardoperation.service.VerifyCreditCardOperationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Use the {@link applyextra.commons.action.VerifyAction }
 */
@Deprecated
@Component
@Slf4j
@Lazy
public class VerifyEvent extends AbstractEvent {
    private static final String SERVICE_NAME_VERIFY_OPERATION = "VerifyOperation";
    private static final BigInteger REQUESTER_TYPE = BigInteger.ONE;

    @Resource
    private VerifyCreditCardOperationService verifyCreditCardOperationService;

    @Override
    protected EventOutput execution(final Object... eventInput) {
        final VerifyEventDTO flowDTO = (VerifyEventDTO) eventInput[0];

        final VerifyCreditCardRequest verifyCreditCardRequest = new VerifyCreditCardRequest();
        verifyCreditCardRequest.setOperationCode(flowDTO.getVerificationCode());
        verifyCreditCardRequest.setArrangementId(flowDTO.getArrangementId());
        verifyCreditCardRequest.setArrangementType(flowDTO.getArrangementType());
        verifyCreditCardRequest.setBeneficiaryId(flowDTO.getCustomerId());
        verifyCreditCardRequest.setBeneficiaryType(REQUESTER_TYPE);
        verifyCreditCardRequest.setRequesterId(flowDTO.getCustomerId());
        verifyCreditCardRequest.setRequesterType(REQUESTER_TYPE);

        final VerifyCreditCardOperationRequest verifyCreditCardOperationRequest = new VerifyCreditCardOperationRequest();
        verifyCreditCardOperationRequest.setRequestID(verifyCreditCardRequest);
        final VerifyCreditCardOperationResponse verifyCreditCardOperationResponse;

        try {
            verifyCreditCardOperationResponse = this.verifyCreditCardOperationService
                    .verifyCreditCardOperationResponse(verifyCreditCardOperationRequest);
        } catch (final IntegrationException ex) {
            throw new ActivityException(SERVICE_NAME_VERIFY_OPERATION, "Could not verify request, exception occurred", ex);
        }

        flowDTO.setVerificationStatus(verifyCreditCardOperationResponse.getVerificationStatus().getVerificationStatus());

        if ("YES".equals(flowDTO.getVerificationStatus())) {
            return new EventOutput(Result.SUCCESS);
        } else {
            List<RejectReasonDTO> rejectReasonList = new ArrayList<>();
            for (RejectReasonType reasonList : verifyCreditCardOperationResponse.getVerificationStatus().getRejectReason()) {
                RejectReasonDTO rejectReasonDTO = new RejectReasonDTO();
                rejectReasonDTO.setCode(reasonList.getReasonCode().toString());
                rejectReasonDTO.setDescription(reasonList.getReasonDescription());
                rejectReasonList.add(rejectReasonDTO);
            }
            flowDTO.getRejectReason().addAll(rejectReasonList);
            if (rejectReasonList.size() > 0) {
                log.warn("Request with Id " + flowDTO.getRequestId() + " was declined in VerifyOperation with reason: " + rejectReasonList.get(0).getDescription());
            } else {
                log.warn("Request with Id " + flowDTO.getRequestId() + " was declined in VerifyOperation unknown reason");
            }
            return new EventOutput(Result.FAILURE);
        }

    }

    public interface VerifyEventDTO {
        String getArrangementId();
        BigInteger getArrangementType();
        BigInteger getVerificationCode();

        String getRequestId();
        String getCustomerId();
        String getVerificationStatus();
        List<RejectReasonDTO> getRejectReason();
        void setVerificationStatus(String verificationStatus);
    }

}
