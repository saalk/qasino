package applyextra.operations.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import applyextra.operations.event.VerifyEvent;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VerifyDTO implements VerifyEvent.VerifyEventDTO {

    private String requestId;
    private String arrangementId;
    private BigInteger verificationCode;
    private String beneficiaryId;
    private BigInteger beneficiaryType;
    private BigInteger requesterType;
    private String verificationStatus;
    @Setter(AccessLevel.NONE)
    private List<RejectReasonDTO> rejectReason;
    private String customerId;

    private BigInteger arrangementType;

    public List<RejectReasonDTO> getRejectReason() {
        if (rejectReason == null) {
            rejectReason = new ArrayList<>();
        }
        return rejectReason;
    }
}
