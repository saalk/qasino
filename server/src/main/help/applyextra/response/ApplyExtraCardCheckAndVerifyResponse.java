package applyextra.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import applyextra.configuration.Constants;
import applyextra.model.ApplyExtraCardDTO;
import applyextra.businessrules.MagDatRule;
import applyextra.commons.resource.ErrorCodes;
import applyextra.commons.response.CreditcardFrontendResponse;
import applyextra.operations.dto.RejectReasonDTO;

import java.util.List;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplyExtraCardCheckAndVerifyResponse implements CreditcardFrontendResponse<ApplyExtraCardDTO> {

    private boolean passed;
    private String errorCode;

    @Override
    public ApplyExtraCardCheckAndVerifyResponse extract(ApplyExtraCardDTO dto) {

        final List<RejectReasonDTO> rejectReason = dto.getRejectReason();
        if (rejectReason != null && !rejectReason.isEmpty()) { // check result from VerifyEvent
            final RejectReasonDTO rejectDTO = rejectReason.get(0);
            final MagDatRule rule = (MagDatRule) MagDatRule.getRuleTypeByRejectCodeAndReason(
                    rejectDTO.getCode(),
                    rejectDTO.getDescription()
            );
            this.setErrorCode(rule == null ? ErrorCodes.VERIFY_FAILED.toString() : rule.getRuleId());
        } else {
            // Check result from business rules, only applies when verify doesn't fail.
            this.setErrorCode(String.valueOf(dto.getRulesCode()));
        }

        this.setPassed(this.errorCode == null
                || Constants.EMPTY_STRING.equals(this.errorCode)
                || Constants.ZERO_STRING.equals(this.errorCode));
        return this;
    }
}
