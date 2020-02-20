package applyextra.operations.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SiaOrchestrationDTO {

    private String requestor;
    private DateTime requestTimestamp;
    private String requestToken;
    private String inquireAccountCode;
    private DateTime repricingStartDate;
    private final List<SIAAccountInformation> accounts = new ArrayList<>();
    private String inquireAccountPricingCode;
    private String feeCode;
    private DateTime nextAnniversaryDate;
    private BigInteger membershipFeeAmount;
}
