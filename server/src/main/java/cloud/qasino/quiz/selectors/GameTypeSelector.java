package cloud.qasino.quiz.selectors;

import cloud.qasino.quiz.selectors.ing.AccountType;
import cloud.qasino.quiz.selectors.ing.PortfolioCode;
import cloud.qasino.quiz.selectors.ing.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.management.InvalidAttributeValueException;
import java.util.ArrayList;
import java.util.List;

import static cloud.qasino.quiz.selectors.ing.AccountType.*;
import static cloud.qasino.quiz.selectors.ing.PortfolioCode.*;
import static cloud.qasino.quiz.selectors.ing.ProductType.CREDITQUIZ;
import static cloud.qasino.quiz.selectors.ing.ProductType.PLATINUMQUIZ;


/**
 * PricingProgram selector selects the correct list of PricingPrograms that is required to change
 * the 'price' of a creditquiz via the POSITION, MEMBER or REGULAR creditquiz account. <p> The
 * 'price' that can be changed by sending a PricingProgram for a creditquiz account to SIA are: <p>-
 * membershipFee (eg. € 17,40), <p>- interestRate (eg. 12,4%), <p>- minimumPaymentCalculation (eg.
 * 100% or 5%min45e), <p>- atmFee (eg. € 4,50) etc <p> NB Repricing means sending one or more a
 * pricing programs to SIA to replace the current pricing programs.
 */
public class GameTypeSelector {

    public List<String> findAllForChangeRepayment(ProductType checkProductType, AccountType checkAccountType, Integer checkNewCreditLimit, PortfolioCode checkNewPortfolioCode) throws InvalidAttributeValueException {

        checkAttributeValueConsistency(checkProductType, checkNewPortfolioCode);
        return findAll(checkProductType, checkAccountType, checkNewCreditLimit, checkNewPortfolioCode);
    }

    public List<String> findAllForChangeLimit(ProductType checkProductType, AccountType checkAccountType, int checkNewCreditLimit) {

        return findAll(checkProductType, checkAccountType, checkNewCreditLimit, null);
    }

    private List<String> findAll(ProductType checkProductType, AccountType checkAccountType, Integer checkNewCreditLimit, PortfolioCode checkNewPortfolioCode) {

        List<String> selected = new ArrayList<>();
        String found;

        for (PricingProgram findPricingProgram : PricingProgram.values()) {
            found = PricingProgram.find(findPricingProgram, checkProductType, checkAccountType, checkNewCreditLimit, checkNewPortfolioCode);
            if (found != null) {
                selected.add(found);
            }
        }
        return selected;
    }

    private void checkAttributeValueConsistency(ProductType checkProductType, PortfolioCode checkNewPortfolioCode) throws InvalidAttributeValueException {
        if (checkProductType == PLATINUMQUIZ) {
            if (checkNewPortfolioCode == CREDITQUIZ_CHARGE || checkNewPortfolioCode == CREDITQUIZ_REVOLVING) {
                throw new InvalidAttributeValueException("Supplied ProductType PLATINUMQUIZ not consistent with supplied PortfolioCode CREDITQUIZ");
            }
        } else {
            if (checkNewPortfolioCode == PLATINUMQUIZ_CHARGE || checkNewPortfolioCode == PLATINUMQUIZ_REVOLVING) {
                throw new InvalidAttributeValueException("Supplied ProductType CREDITQUIZ not consistent with supplied PortfolioCode PLATINUMQUIZ");
            }
        }
    }

    @AllArgsConstructor
    @Getter
    public enum PricingProgram {

        // TO_100 = 100 % repayment every month
        REPRICING_01_REPAYMENT_TO_100(CREDITQUIZ, REGULAR, null, CREDITQUIZ_CHARGE, "RFUL01"),
        REPRICING_02_REPAYMENT_TO_100(PLATINUMQUIZ, REGULAR, null, PLATINUMQUIZ_CHARGE, "RFUL01"),
        REPRICING_03_REPRICING_TO_100(CREDITQUIZ, POSITION, null, CREDITQUIZ_CHARGE, "RNWEBF"),
        REPRICING_04_REPAYMENT_TO_100(PLATINUMQUIZ, POSITION, null, PLATINUMQUIZ_CHARGE, "RNWEBF"),
        REPRICING_05_REPRICING_TO_100(CREDITQUIZ, MEMBER, null, CREDITQUIZ_CHARGE, "RNWEBF"),
        REPRICING_06_REPAYMENT_TO_100(PLATINUMQUIZ, MEMBER, null, PLATINUMQUIZ_CHARGE, "RNWEBF"),

        // TO_5MIN45 = 5% with a minimum of 45 euro every month
        REPRICING_07_REPAYMENT_TO_5MIN45(CREDITQUIZ, REGULAR, null, CREDITQUIZ_REVOLVING, "RREV01"),
        REPRICING_08_REPAYMENT_TO_5MIN45(PLATINUMQUIZ, REGULAR, null, PLATINUMQUIZ_REVOLVING, "RREV02"),
        REPRICING_09_REPAYMENT_TO_5MIN45(CREDITQUIZ, POSITION, null, CREDITQUIZ_REVOLVING, "RNWEBR"),
        REPRICING_10_REPAYMENT_TO_5MIN45(PLATINUMQUIZ, POSITION, null, PLATINUMQUIZ_REVOLVING, "RNWEGR"),
        REPRICING_11_REPAYMENT_TO_5MIN45(CREDITQUIZ, MEMBER, null, CREDITQUIZ_REVOLVING, "RNWEBR"),
        REPRICING_12_REPAYMENT_TO_5MIN45(PLATINUMQUIZ, MEMBER, null, PLATINUMQUIZ_REVOLVING, "RNWEGR"),

        // TO_139INTEREST = lower limits have higher interest rates
        REPRICING_13_INTEREST_TO_139(CREDITQUIZ, REGULAR, LimitType.LESS, null, "RINT01"),
        REPRICING_14_INTEREST_TO_139(PLATINUMQUIZ, REGULAR, LimitType.LESS, null, "RINT01"),
        REPRICING_15_INTEREST_TO_139(CREDITQUIZ, POSITION, LimitType.LESS, null, "RINTBL"),
        REPRICING_16_INTEREST_TO_139(PLATINUMQUIZ, POSITION, LimitType.LESS, null, "RINTBL"),
        REPRICING_17_INTEREST_TO_139(CREDITQUIZ, MEMBER, LimitType.LESS, null, "RINTBL"),
        REPRICING_18_INTEREST_TO_139(PLATINUMQUIZ, MEMBER, LimitType.LESS, null, "RINTBL"),

        // TO_124INTEREST = higher limits have higher interest rates
        REPRICING_19_INTEREST_TO_124(CREDITQUIZ, REGULAR, LimitType.EQUAL_OR_MORE, null, "RINT07"),
        REPRICING_20_INTEREST_TO_124(PLATINUMQUIZ, REGULAR, LimitType.EQUAL_OR_MORE, null, "RINT07"),
        REPRICING_21_INTEREST_TO_124(CREDITQUIZ, POSITION, LimitType.EQUAL_OR_MORE, null, "RINTGD"),
        REPRICING_22_INTEREST_TO_124(PLATINUMQUIZ, POSITION, LimitType.EQUAL_OR_MORE, null, "RINTGD"),
        REPRICING_23_INTEREST_TO_124(CREDITQUIZ, MEMBER, LimitType.EQUAL_OR_MORE, null, "RINTGD"),
        REPRICING_24_INTEREST_TO_124(PLATINUMQUIZ, MEMBER, LimitType.EQUAL_OR_MORE, null, "RINTGD");

        private static int LIMIT_BOUNDARY = 5000;

        private ProductType productType;
        private AccountType accountType;
        private LimitType creditLimit;
        private PortfolioCode portfolioCode;
        public String resultingPricingProgram;

        private static String find(PricingProgram findPricingProgram, ProductType checkProductType, AccountType checkAccountType, Integer checkCreditLimit, PortfolioCode checkPortfolioCode) {

            if (matchingProductType(findPricingProgram, checkProductType) &&
                    matchingAccountType(findPricingProgram, checkAccountType) &&
                    matchingCreditLimit(findPricingProgram, checkCreditLimit) &&
                    matchingPortfolioCode(findPricingProgram, checkPortfolioCode)) {
                return findPricingProgram.resultingPricingProgram;
            }
            return null;
        }

        static boolean matchingProductType(PricingProgram matchPricingProgram, ProductType checkProductType) {
            return matchPricingProgram.getProductType().equals(checkProductType);
        }

        static boolean matchingAccountType(PricingProgram matchPricingProgram, AccountType checkAccountType) {
            return matchPricingProgram.getAccountType().equals(checkAccountType);
        }

        static boolean matchingCreditLimit(PricingProgram matchPricingProgram, Integer newCreditLimit) {
            if (matchPricingProgram.getCreditLimit() == null) {
                // null means 'any' -> always a positive match
                return true;
            }
            if (newCreditLimit == null) {
                return matchPricingProgram.getCreditLimit() == null;
            }
            if (matchPricingProgram.getCreditLimit() == LimitType.EQUAL_OR_MORE) {
                return newCreditLimit >= LIMIT_BOUNDARY;
            } else {
                return newCreditLimit < LIMIT_BOUNDARY;
            }
        }

        static boolean matchingPortfolioCode(PricingProgram matchPricingProgram, PortfolioCode newPortfolioCode) {
            if (matchPricingProgram.getPortfolioCode() == null) {
                // null means 'any' -> always a positive match
                return true;
            }
            if (newPortfolioCode == null) {
                return matchPricingProgram.getPortfolioCode() == null;
            }
            return matchPricingProgram.getPortfolioCode().equals(newPortfolioCode);
        }

        public enum LimitType {LESS, EQUAL_OR_MORE}
    }
}


