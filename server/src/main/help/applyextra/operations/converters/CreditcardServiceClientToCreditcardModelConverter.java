package applyextra.operations.converters;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

@Component
public class CreditcardServiceClientToCreditcardModelConverter {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final int START_LAST_CARDNUMBER_SECTION = 12;
    private static final String CC = "(\\d){16}";
    private static final Pattern CC_PATTERN = Pattern.compile(CC);

    public applyextra.commons.model.CreditCard convert(nl.ing.serviceclient.lecca.dto.CreditCard creditCardFrom) {

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        applyextra.commons.model.CreditCard creditCardTo = new applyextra.commons.model.CreditCard();

        creditCardTo.setName(creditCardFrom.getName());
        creditCardTo.setType(creditCardFrom.getType());

        creditCardTo.setStatus(creditCardFrom.getStatus());

        creditCardTo.setIbanNumber(creditCardFrom.getIbanNumber());
        creditCardTo.setProductType(creditCardFrom.getProductType());

        creditCardTo.setAccountNumber(creditCardFrom.getAccountNumber());

        creditCardTo.setCreditCardNumber(creditCardFrom.getCreditCardNumber());
        creditCardTo.setMaskedCreditcardNumber(maskCreditCardNumber(creditCardFrom.getCreditCardNumber()));
        creditCardTo.setId(convertCardId(creditCardFrom.getCreditCardNumber()));

        creditCardTo.setArrangementNumber(creditCardFrom.getArrangementNumber());
        creditCardTo.setSubArrangementNumber(creditCardFrom.getSubArrangementNumber());

        creditCardTo.setStartDate(simpleDateFormat.format(creditCardFrom.getStartDate()));
        creditCardTo.setExpirationDate(simpleDateFormat.format(creditCardFrom.getExpirationDate()));
        
        creditCardTo.setAssignedCreditLimit(creditCardFrom.getAssignedCreditLimit());
        creditCardTo.setBalanceAmount(creditCardFrom.getBalanceAmount());
        creditCardTo.setPortfolioCode(creditCardFrom.getPortfolioCode());

        return creditCardTo;
    }

    public String convertCardId(final String creditCardNumber) {
        String lastFourDigits = "";
        if (isValidCreditcardNumber(creditCardNumber)) {
            lastFourDigits = creditCardNumber.substring(START_LAST_CARDNUMBER_SECTION);
            return lastFourDigits;
        }
        return lastFourDigits;
    }

    public String maskCreditCardNumber(final String creditCardNumber) {
        final StringBuilder maskedCC = new StringBuilder("");
        if (isValidCreditcardNumber(creditCardNumber)) {
            maskedCC.append(creditCardNumber.substring(0, 4))
                    .append(".****.****.")
                    .append(creditCardNumber.substring(START_LAST_CARDNUMBER_SECTION));
            return maskedCC.toString();
        }
        return maskedCC.toString();
    }

    /**
     * @param creditcardNumber
     *            The cc no.
     * @return True if it matches the mask for credit playingcard numbers.
     */
    public boolean isValidCreditcardNumber(final String creditcardNumber) {
        if (creditcardNumber != null) {
            return CC_PATTERN.matcher(creditcardNumber).matches();
        }
        return false;
    }

}
