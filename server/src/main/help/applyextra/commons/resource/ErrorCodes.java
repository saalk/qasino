package applyextra.commons.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import applyextra.commons.orchestration.EventHandlingResponse;

@AllArgsConstructor
public enum ErrorCodes implements EventHandlingResponse {
    // @formatter:off
    /**
     * Business Rules
     */
    CARD_STATUS_IS_NOT_ACTIVATED_RULE                   (501),
    CARD_STATUS_IS_NOT_TEMP_BLOCKED_RULE                (502),
    CARD_STATUS_IS_NOT_INVALID_PIN_RULE                 (503),
    ACCOUNT_STATUS_IS_NOT_BLOCKED_RULE                  (504),
    REISSUE_PERIOD_RULE                                 (505),
    MAXIMUM_REQUESTS_PER_YEAR_RULE                      (506),
    ACCOUNT_STATUS_IS_NOT_CLOSED_RULE                   (507),
    ACCOUNT_STATUS_IS_NOT_CANCELLED_RULE                (508),
    ACCOUNT_IS_NOT_STUDENT_RULE                         (509),
    REQUESTOR_HAS_ROLE_RULE                             (510),
    CARD_PORTFOLIO_CODE_EQUALS_RULE                     (511),
    ACCOUNT_IS_NOT_AFBETALING_CONTINU_LIMIET_RULE       (512),
    ACCOUNT_IS_NOT_OVERSTAPSERVICE_GAANDE_REKENING_RULE (513),
    ACCOUNT_IS_NOT_STUDENT_RULE_SIA                     (514),
    APPLY_REPRICING_DAYS_SINCE_RULE                     (515),
    CARD_STATUS_IS_NOT_BLOCKED_RULE                     (516),
    DAYS_SINCE_RULE                                     (517),
    PENDING_REQUESTS_RULE                               (518),
    CARD_VALIDITY_REMAINING_RULE                        (519),
    ACTIVATION_TOO_CLOSE_TO_EXPIRATION_RULE             (520),
    CARD_IS_NOT_ACTIVATED_RULE                          (521),
    CARD_STATUS_IS_NOT_CLOSED_RULE                      (522),
    EXCEEDED_MAX_PRODUCT_LIMIT                          (523),
    BANK_ACCOUNT_IS_IN_TRANSFER                         (524),
    MAIN_CARD_MINIMUM_YEARS_OLD_RULE                    (525),
    EXTRA_CARD_MINIMUM_YEARS_OLD_RULE                   (526),
    ACCOUNT_HAS_NO_MAIN_CARD_RULE                       (527),
    MAXIMUM_NUMBER_OF_CARDS_RULE                        (528),
    WRONG_PACKAGE_REQUESTED_RULE						(529),
    BENEFICIARY_IS_REQUESTOR_RULE						(530),
    REQUEST_IN_PROCESS_RULE                             (531),
    PAYMENTACCOUNT_CARDTYPE_MISMATCH                    (532),
    PENDING_REPAYMENT_REQUEST                           (533),
    EXTRA_CARD_IS_NOT_ALLOWED_TWICE(543),
    INCOME_TOO_LOW                                      (650),
    STUDENT_INCOME_TOO_LOW                              (651),
    STUDENT_OVERDRAFT_INCOME_TOO_LOW                    (652),
    CHANGE_PROCESS_PERFORMED_RULE                       (653),

    /**
     * Remaining error codes
     */
    NONE                        (0),
    VERIFY_FAILED               (1),
    VERIFY_BENEFICIARY_FAILED   (2),
    UNAUTHORIZED                (403),
    PAGE_NOT_FOUND              (404),
    CONCURRENT_REQUEST          (601),    
    INVALID_REQUEST             (602),
    SCORING_FAILED              (603),
    CAPACITY_CHECK              (604),
    CONFIRM_CHECK_PASSED        (605),
    BKR_CHECK                   (606),
    SUBMIT_FAILED               (607),
    FULFILLMENT_FAILED          (608),
    DISTRIBUTE_FAILED           (609),
    EXCEPTION_VERIFY            (610),
    EXCEPTION_CHECK             (611),
    EXCEPTION_APPROVE           (611),

    CREDIT_LIMIT_NOT_ALLOWED    (612),
    FINANCIAL_ACC_DECLINED      (613),

    RUNTIME_EXCEPTION           (701),
    WEB_APPLICATION_EXCEPTION   (900),

    //SUCCESS
    HTTP_OK                     (200);
    
    // @formatter:on
    @Getter
    private int status;

    @Override
    public String toString() {
        return String.valueOf(status);
    }
}