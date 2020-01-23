package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.businessrules.*;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.orchestration.Action;
import applyextra.commons.resource.ErrorCodes;
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

import static applyextra.commons.resource.ErrorCodes.VERIFY_FAILED;

/**
 * Created by CL94WQ on 25-9-2017.
 */
@Component
@Slf4j
@Lazy
public class VerifyAction implements Action<VerifyAction.VerifyActionDTO, RuleEvaluationResult> {

    private static final String VERIFICATION_STATUS_YES = "YES";
    private static final String SERVICE_NAME_VERIFY_OPERATION = "VerifyOperation";
    private static final BigInteger REQUESTER_TYPE = BigInteger.ONE;

    @Resource
    private VerifyCreditCardOperationService verifyCreditCardOperationService;

    @Override
    public RuleEvaluationResult perform(VerifyActionDTO flowDTO) {

        final VerifyCreditCardOperationRequest verifyCreditCardOperationRequest = createVerifyCreditCardOperationRequest(flowDTO);

        final VerifyCreditCardOperationResponse verifyCreditCardOperationResponse;
        try {
            verifyCreditCardOperationResponse = this.verifyCreditCardOperationService
                    .verifyCreditCardOperationResponse(verifyCreditCardOperationRequest);
        } catch (final IntegrationException ex) {
            throw new ActivityException(SERVICE_NAME_VERIFY_OPERATION, "Could not verify request, exception occurred", ex);
        }

        flowDTO.setVerificationStatus(verifyCreditCardOperationResponse.getVerificationStatus().getVerificationStatus());

        //evaluate response
        RuleEvaluationSettings evaluationSettings = flowDTO.getRuleEvaluationSettings();

        OverallRuleEvaluationResponse evaluationResponse = new OverallRuleEvaluationResponse();

        if (VERIFICATION_STATUS_YES.equals(flowDTO.getVerificationStatus())) {
            return RuleEvaluationResult.PASSED;
        } else {
            List<RejectReasonDTO> rejectReasonList = new ArrayList<>();
            for (RejectReasonType rejectReasonType : verifyCreditCardOperationResponse.getVerificationStatus().getRejectReason()) {
                Rule rejectedRule = MagDatRule.getRuleTypeByRejectCodeAndReason(
                        rejectReasonType.getReasonCode(), rejectReasonType.getReasonDescription());

                if (evaluationSettings != null && evaluationSettings.allowOverruling(rejectedRule.getRuleId())) {
                    evaluationResponse.overrule(rejectedRule, rejectReasonType.getReasonCode().toString(), rejectReasonType
                            .getReasonDescription());
                } else {
                    evaluationResponse.reject(rejectedRule, rejectReasonType.getReasonCode().toString(), rejectReasonType
                            .getReasonDescription());
                }
                flowDTO.setEvaluationResponse(VERIFY_FAILED, evaluationResponse);

                RejectReasonDTO rejectReasonDTO = new RejectReasonDTO();
                rejectReasonDTO.setCode(rejectReasonType.getReasonCode().toString());
                rejectReasonDTO.setDescription(rejectReasonType.getReasonDescription());
                rejectReasonList.add(rejectReasonDTO);
            }

            flowDTO.getRejectReason().addAll(rejectReasonList);
            if (rejectReasonList.size() > 0) {
                log.warn("Request with Id " + flowDTO.getRequestId() + " was declined in VerifyOperation with reason: " + rejectReasonList.get(0).getDescription());
            } else {
                log.warn("Request with Id " + flowDTO.getRequestId() + " was declined in VerifyOperation unknown reason");
            }

            return evaluationResponse.getResult();
        }
    }

    private VerifyCreditCardOperationRequest createVerifyCreditCardOperationRequest(final VerifyActionDTO flowDTO) {

        final VerifyCreditCardRequest verifyCreditCardRequest = new VerifyCreditCardRequest();
        verifyCreditCardRequest.setOperationCode(flowDTO.getVerificationCode());
        verifyCreditCardRequest.setArrangementId(flowDTO.getArrangementId());
        verifyCreditCardRequest.setArrangementType(flowDTO.getArrangementType());
        verifyCreditCardRequest.setBeneficiaryId((flowDTO.getBeneficiaryId() == null) ? flowDTO.getCustomerId() : flowDTO.getBeneficiaryId());
        verifyCreditCardRequest.setBeneficiaryType(REQUESTER_TYPE);
        verifyCreditCardRequest.setRequesterId(flowDTO.getCustomerId());
        verifyCreditCardRequest.setRequesterType(REQUESTER_TYPE);

        final VerifyCreditCardOperationRequest verifyCreditCardOperationRequest = new VerifyCreditCardOperationRequest();
        verifyCreditCardOperationRequest.setRequestID(verifyCreditCardRequest);
        return verifyCreditCardOperationRequest;
    }

    public interface VerifyActionDTO {
        String getArrangementId();

        BigInteger getArrangementType();

        BigInteger getVerificationCode();

        String getBeneficiaryId();

        String getCustomerId();

        String getVerificationStatus();

        void setVerificationStatus(String verificationStatus);

        RuleEvaluationSettings getRuleEvaluationSettings();

        void setEvaluationResponse(final ErrorCodes overallErrorCode, final OverallRuleEvaluationResponse response);

        List<RejectReasonDTO> getRejectReason();

        String getRequestId();
    }
}
