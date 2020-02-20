package applyextra.commons.resource;

public interface RegistrationConstants {

    enum Keys {
        //C1 DATA
        CORRELATION_ID("correlationId"),
        APP_ID("appId"),
        CUSTOMER_ID("customerId"),
        CREDIT_CARD_OPERATION("registrationOperationType"),
        PROCESS_TYPE("processType"),
        USER_ID("userId"),
        LOAN_AMOUNT("loanAmount"),
        //C3 DATA
        CREDIT_CARD_NUMBER("creditCardNumber"),
        OLD_VALUE("oldValue"),
        NEW_VALUE("newValue"),
        SIA_ACCOUNT_NUMBER("siaAccountNumber");

        private String key;

        Keys(String key) { this.key = key; }
        public String getKey() { return key; }
    }
}
