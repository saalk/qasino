package applyextra.operations.dto;

import lombok.Getter;

public enum CramStatus {

    INITIATED("Initiated"),
    DRAFT("Draft"),
    SUBMITTED("Submitted"),
    AUTHORISED("Authorised"),
    EXECUTION("Execution"),
    FULFILLED("Fulfilled"),
    DECLINED("Declined"),
    EXPIRED("Expired"),
    CANCELLED("Cancelled");

    @Getter
    private final String statusString;

    CramStatus(String statusString) {
        this.statusString = statusString;
    }

    public static CramStatus fromString(String statusString){ return CramStatus.valueOf(statusString.toUpperCase()); }

}
