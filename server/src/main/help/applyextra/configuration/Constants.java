    package applyextra.configuration;

    import applyextra.commons.state.CreditCardsStateMachine;

    import java.math.BigInteger;
    import java.util.Arrays;
    import java.util.Collections;
    import java.util.List;

    import static applyextra.commons.state.CreditCardsStateMachine.State.*;

    public interface Constants {

        String CONSUMER_APPLICATION_NAME = "pCreditcardsApplyExtraCardAPI";
        String NAME_APPLY_EXTRA_CARD = "ApplyExtraCard";
        String APPLICATION_ID = "620";

        // Tags to send along are:
            //<ProductName>
            //<CreditLimitAmount>
        String SEND_CUSTOMER_MESSAGE_TEMPLATE_ID = "SCRDBA001";

        BigInteger VERIFYEVENT_VERIFICATION_CODE_CUSTOMER = new BigInteger("1400010");
        BigInteger VERIFYEVENT_VERIFICATION_CODE_BENEFICIARY = new BigInteger("1400011");
        BigInteger VERIFYEVENT_ARRANGEMENTTYPE_FOR_IBAN = new BigInteger("4");


        String EMPTY_STRING = "";
        String ZERO_STRING = "0";

        String TEST = "TEST";
        String ACCEPTANCE="ACCEPT";
        String PROD = "PROD";
        String TOPIC = "applyextracard";

        String DATE_TIME_FORMAT_DD_MM_YYYY = "dd-MM-yyyy";


        // Not checking for Authorized for some reason ~ Vamsi.
        List<CreditCardsStateMachine.State> PENDING_STATES = Collections.unmodifiableList(
                Arrays.asList(AUTHORIZED, RECHECKED, REVERIFIED, EXECUTED, FULFILLED_CUSTOMER, FULFILLED)
        );

        List<CreditCardsStateMachine.State> CANCEL_REQUEST_STATE = Collections.unmodifiableList(
                Arrays.asList(
                    CreditCardsStateMachine.State.LISTED,
                    CreditCardsStateMachine.State.CHECKED,
                    CreditCardsStateMachine.State.VERIFIED,
                    CreditCardsStateMachine.State.SUBMITTED)
        );
        String SECONDARY_ACCOUNT_NUMBER = "secondaryAccountNumber";

    }
