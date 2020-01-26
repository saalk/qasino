package applyextra.operations.converters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import applyextra.commons.model.AccountCategory;
import applyextra.commons.model.PortfolioCode;
import applyextra.commons.model.RepricingProgram;

import java.util.ArrayList;
import java.util.List;

import static applyextra.commons.model.AccountCategory.*;
import static applyextra.commons.model.PortfolioCode.*;
import static applyextra.commons.model.RepricingProgram.*;
import static applyextra.operations.converters.PricingProgramSelector.LimitType.*;

/**
 * PricingProgram selector selects the correct list of RepricingPrograms that are required to change
 * the 'price' of a creditcard via the POSITION, MEMBER or REGULAR creditcard account. The
 * 'price' changes that can be changed by sending a RepricingProgram for a creditcard account to SIA are: <p>-
 * membershipFee (eg. € 17,40), <p>- interestRate (eg. 12,4%), <p>- minimumPaymentCalculation (eg.
 * 100% or 5%min45e), <p>- atmFee (eg. € 4,50) etc <p> NB Repricing means sending one or more a
 * pricing programs to SIA to change 'prices'.
 */

@AllArgsConstructor
@Getter
public enum PricingProgramSelector {

    // TO_100 = PricingProgram to change to 100 % repayment every month
    REPRICING_01_REPAYMENT_TO_100(CREDITCARD_CHARGE, REGULAR, ANYLIMIT, CREDITCARD_CHARGE_REGULAR_ANYLIMIT),
    REPRICING_02_REPAYMENT_TO_100(PLATINUMCARD_CHARGE, REGULAR, ANYLIMIT, PLATINUMCARD_CHARGE_REGULAR_ANYLIMIT),
    REPRICING_03_REPAYMENT_TO_100(CREDITCARD_CHARGE, POSITION, ANYLIMIT, CREDITCARD_CHARGE_POSITION_ANYLIMIT),
    REPRICING_04_REPAYMENT_TO_100(PLATINUMCARD_CHARGE, POSITION, ANYLIMIT, PLATINUMCARD_CHARGE_POSITION_ANYLIMIT),
    REPRICING_05_REPAYMENT_TO_100(CREDITCARD_CHARGE, MEMBER, ANYLIMIT, CREDITCARD_CHARGE_MEMBER_ANYLIMIT),
    REPRICING_06_REPAYMENT_TO_100(PLATINUMCARD_CHARGE, MEMBER, ANYLIMIT, PLATINUMCARD_CHARGE_MEMBER_ANYLIMIT),

    // TO_5MIN45 = PricingProgram to change to 5% with a minimum of 45 euro every month
    REPRICING_07_REPAYMENT_TO_5MIN45(CREDITCARD_REVOLVING, REGULAR, ANYLIMIT, CREDITCARD_REVOLVING_REGULAR_ANYLIMIT),
    REPRICING_08_REPAYMENT_TO_5MIN45(PLATINUMCARD_REVOLVING, REGULAR, ANYLIMIT, PLATINUMCARD_REVOLVING_REGULAR_ANYLIMIT),
    REPRICING_09_REPAYMENT_TO_5MIN45(CREDITCARD_REVOLVING, POSITION, ANYLIMIT, CREDITCARD_REVOLVING_POSITION_ANYLIMIT),
    REPRICING_10_REPAYMENT_TO_5MIN45(PLATINUMCARD_REVOLVING, POSITION, ANYLIMIT, PLATINUMCARD_REVOLVING_POSITION_ANYLIMIT),
    REPRICING_11_REPAYMENT_TO_5MIN45(CREDITCARD_REVOLVING, MEMBER, ANYLIMIT, CREDITCARD_REVOLVING_MEMBER_ANYLIMIT),
    REPRICING_12_REPAYMENT_TO_5MIN45(PLATINUMCARD_REVOLVING, MEMBER, ANYLIMIT, PLATINUMCARD_REVOLVING_MEMBER_ANYLIMIT),

    // TO_139INTEREST = PricingProgram to change to 13,9 interest rate
    REPRICING_13_INTEREST_TO_139(CREDITCARD_REVOLVING, REGULAR, LOW, CREDITCARD_REVOLVING_REGULAR_LOW),
    REPRICING_14_INTEREST_TO_139(PLATINUMCARD_REVOLVING, REGULAR, LOW, PLATINUMCARD_REVOLVING_REGULAR_LOW),
    REPRICING_15_INTEREST_TO_139(CREDITCARD_REVOLVING, POSITION, LOW, CREDITCARD_REVOLVING_POSITION_LOW),
    REPRICING_16_INTEREST_TO_139(PLATINUMCARD_REVOLVING, POSITION, LOW, PLATINUMCARD_REVOLVING_POSITION_LOW),
    REPRICING_17_INTEREST_TO_139(CREDITCARD_REVOLVING, MEMBER, LOW, CREDITCARD_REVOLVING_MEMBER_LOW),
    REPRICING_18_INTEREST_TO_139(PLATINUMCARD_REVOLVING, MEMBER, LOW, PLATINUMCARD_REVOLVING_MEMBER_LOW),

    // TO_124INTEREST = PricingProgram to change to  12,4 interest rate
    REPRICING_19_INTEREST_TO_124(CREDITCARD_REVOLVING, REGULAR, HIGH, CREDITCARD_REVOLVING_REGULAR_HIGH),
    REPRICING_20_INTEREST_TO_124(PLATINUMCARD_REVOLVING, REGULAR, HIGH, PLATINUMCARD_REVOLVING_REGULAR_HIGH),
    REPRICING_21_INTEREST_TO_124(CREDITCARD_REVOLVING, POSITION, HIGH, CREDITCARD_REVOLVING_POSITION_HIGH),
    REPRICING_22_INTEREST_TO_124(PLATINUMCARD_REVOLVING, POSITION, HIGH, PLATINUMCARD_REVOLVING_POSITION_HIGH),
    REPRICING_23_INTEREST_TO_124(CREDITCARD_REVOLVING, MEMBER, HIGH, CREDITCARD_REVOLVING_MEMBER_HIGH),
    REPRICING_24_INTEREST_TO_124(PLATINUMCARD_REVOLVING, MEMBER, HIGH, PLATINUMCARD_REVOLVING_MEMBER_HIGH);

    public enum LimitType {ANYLIMIT, LOW, HIGH}

    private static int LIMIT_TYPE_BOUNDARY = 5000;

    private PortfolioCode portfolioCode;
    private AccountCategory accountCategory;
    private LimitType creditLimit;
    private RepricingProgram resultingRepricingProgram;

    /**
     * <p> How to use for the different processes: <ul><li> Change Limit: use findAll with 3 parameters: this supplies 2 pricing
     * programs (including the repayment 'anylimit' that just overwrites an existing repayment setting). <li> Change Repayment Off :
     * with 2 parameters: this supplies 1 pricing programs (only the repayment 'anylimit'). <li> Change Repayment Off : with 3
     * parameters: this supplies 2 pricing programs (including the interest 'high' of 'low' that just overwrites an existing
     * interest setting). <li> Change Repayment On : with 3 parameters: this supplies 2 pricing programs (including the interest
     * 'high' of 'low' to set the interest).  <li> Change Repayment On : with 2 parameters: no allowed as this supplies 1 pricing
     * programs (not including setting an interest for 'high' or 'low' interest rate).</ul>
     */
    public static List<RepricingProgram> findAll(PortfolioCode checkPortfolioCode, AccountCategory checkAccountCategory) {
        // no limit supplied, only test portfoliocode and accountcategory
        return findAll(checkPortfolioCode, checkAccountCategory, null);
    }

    /**
     * <p> How to use for the different processes: <ul><li> Change Limit: use findAll with 3 parameters: this supplies 2 pricing
     * programs (including the repayment 'anylimit' that just overwrites an existing repayment setting). <li> Change Repayment Off :
     * with 2 parameters: this supplies 1 pricing programs (only the repayment 'anylimit'). <li> Change Repayment Off : with 3
     * parameters: this supplies 2 pricing programs (including the interest 'high' of 'low' that just overwrites an existing
     * interest setting). <li> Change Repayment On : with 3 parameters: this supplies 2 pricing programs (including the interest
     * 'high' of 'low' to set the interest).  <li> Change Repayment On : with 2 parameters: no allowed as this supplies 1 pricing
     * programs (not including setting an interest for 'high' or 'low' interest rate).</ul>
     */
    public static List<RepricingProgram> findAll(PortfolioCode checkPortfolioCode, AccountCategory checkAccountCategory, Integer checkCreditLimit) {

        List<RepricingProgram> found = new ArrayList<>();
        for (PricingProgramSelector findRepricingProgram : PricingProgramSelector.values()) {
            if (findRepricingProgram.matching(checkPortfolioCode, checkAccountCategory, checkCreditLimit)) {
                found.add(findRepricingProgram.getResultingRepricingProgram());
            }
        }
        return found;
    }

    private boolean matching(PortfolioCode checkPortfolioCode, AccountCategory checkAccountCategory, Integer checkCreditLimit) {

        boolean result = portfolioCode.equals(checkPortfolioCode) && accountCategory.equals(checkAccountCategory);

        if (checkCreditLimit == null) {
            // no limit supplied, only test portfoliocode and accountcategory
            // do not include the HIGH or LOW pricing programs
            return result && (creditLimit == LimitType.ANYLIMIT);
        }

        switch (creditLimit) {
            case ANYLIMIT:
                return result;
            case HIGH:
                return result && checkCreditLimit >= LIMIT_TYPE_BOUNDARY;
            case LOW:
                return result && checkCreditLimit < LIMIT_TYPE_BOUNDARY;
            default:
                throw new IllegalArgumentException("Unexpected creditlimit found");
        }
    }
}
