package applyextra.commons.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import applyextra.commons.configuration.CreditCardRegistrationsOperation;
import applyextra.commons.configuration.ProcessType;
import applyextra.commons.resource.RegistrationConstants;

import java.util.HashMap;
import java.util.Map;

@Builder
@AllArgsConstructor
@Getter
@ToString
public class BKREvent {

    private final String correlationId;

    private final String appId;

    private final String userId;

    private final String customerId;

    private final CreditCardRegistrationsOperation registrationsOperationType;

    private final ProcessType processType;

    private final String oldValue;

    private final String newValue;

    private final String creditCardNumber;

    private final String siaAccountNumber;

    private final String loanAmount;

    public static Map<String, Object> convertToMap(BKREvent input){
        Map<String, Object> bkrData = new HashMap<>();
            bkrData.put(RegistrationConstants.Keys.CORRELATION_ID.getKey(), input.getCorrelationId());
            bkrData.put(RegistrationConstants.Keys.APP_ID.getKey(), input.getAppId());
            bkrData.put(RegistrationConstants.Keys.USER_ID.getKey(), input.getUserId());
            bkrData.put(RegistrationConstants.Keys.CUSTOMER_ID.getKey(), input.getCustomerId());
            bkrData.put(RegistrationConstants.Keys.CREDIT_CARD_OPERATION.getKey(), String.valueOf(input.getRegistrationsOperationType()));
            bkrData.put(RegistrationConstants.Keys.PROCESS_TYPE.getKey(), String.valueOf(input.getProcessType()));
            bkrData.put(RegistrationConstants.Keys.OLD_VALUE.getKey(), input.getOldValue());
            bkrData.put(RegistrationConstants.Keys.NEW_VALUE.getKey(), input.getNewValue());
            bkrData.put(RegistrationConstants.Keys.CREDIT_CARD_NUMBER.getKey(), input.getCreditCardNumber());
            bkrData.put(RegistrationConstants.Keys.SIA_ACCOUNT_NUMBER.getKey(), input.getSiaAccountNumber());
            bkrData.put(RegistrationConstants.Keys.LOAN_AMOUNT.getKey(), input.getLoanAmount());
        return bkrData;
    }

}