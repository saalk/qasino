package applyextra.commons.util;


import applyextra.api.listaccounts.creditcards.value.*;

public class InquireAccountResponseUtil {

    /**
     * Set the account status
     */
    public static CreditCardAccountStatus convertAccountStatus(String accountStatus) {
        switch (accountStatus) {
            case "AA" :
                return CreditCardAccountStatus.ACTIVE;
            case "CB" :
            case "CG" :
            case "CT" :
            case "RD" :
                return CreditCardAccountStatus.CLOSED;
            case "CA" :
                return CreditCardAccountStatus.CANCELLED;
            case "JD" :
            case "TD" :
            case "TR" :
                return CreditCardAccountStatus.BLOCKED;
            default:
                throw new IllegalArgumentException("Invalid account status: " + accountStatus);
        }
    }

    /**
     * Set the creditcard status
     */
    public static CreditCardStatus convertCreditCardStatus(String creditCardStatus) {
        switch (creditCardStatus) {
            case "AA" :
                return CreditCardStatus.ACTIVE;
            case "CB" :
            case "CP" :
            case "CT" :
            case "FP" :
            case "RD" :
            case "XP" :
            case "ZD" :
                return CreditCardStatus.CLOSED;
            case "LP" :
                return CreditCardStatus.LOST;
            case "CA" :
                return CreditCardStatus.CANCELLED;
            case "JD" :
            case "TD" :
            case "JT" :
            case "PB" :
            case "TR" :
                return CreditCardStatus.BLOCKED;
            case "PD" :
                return CreditCardStatus.INVALIDPIN;
            case "SP" :
                return CreditCardStatus.STOLEN;
            default:
                throw new IllegalArgumentException("Invalid credit playingcard status: " + creditCardStatus);
        }
    }

    /**
     * Convert the plastic type into Primary or Secundary
     */
    public static CreditCardType convertCardType(String plasticType) {
        switch (plasticType) {
            case "NB1" :
            case "NB3" :
            case "NG1" :
            case "NG3" :
            case "NBX" :
            case "NGX" :
                return CreditCardType.PRIMARY;

            case "NB2" :
            case "NB4" :
            case "NG2" :
            case "NG4" :
            case "NBY" :
            case "NGY" :
            case "FD1" :
            case "FD2" :
            case "FD3" :
            case "FD4" :
            case "FD5" :
            case "FD6" :
            case "PBY" :
            case "PBN" :
            case "CBY" :
            case "CBN" :
            case "CGY" :
            case "CGN" :
            case "PGY" :
            case "PGN" :
            case "DUM" :
                return CreditCardType.SECONDARY;
            default:
                throw new IllegalArgumentException("Invalid plastic type: " + plasticType);
        }
    }

    /**
     * Set the portfoliocode, cycleday and productname
     */
    public static void setPortfolioCode(CreditCardAccount creditCardMainAccount, String portfolioCode) {
        switch (portfolioCode) {
            case "PBR" :
                creditCardMainAccount.setCreditCardPortfolioCode(CreditCardPortfolioCode.REVOLVING);
                creditCardMainAccount.setCycleDay(3);
                creditCardMainAccount.setCreditCardProductName(CreditCardProductName.CREDITCARD);
                break;
            case "PBC" :
                creditCardMainAccount.setCreditCardPortfolioCode(CreditCardPortfolioCode.CHARGE);
                creditCardMainAccount.setCycleDay(3);
                creditCardMainAccount.setCreditCardProductName(CreditCardProductName.CREDITCARD);
                break;
            case "PGR" :
                creditCardMainAccount.setCreditCardPortfolioCode(CreditCardPortfolioCode.REVOLVING);
                creditCardMainAccount.setCycleDay(4);
                creditCardMainAccount.setCreditCardProductName(CreditCardProductName.PLATINUMCARD);
                break;
            case "PGC" :
                creditCardMainAccount.setCreditCardPortfolioCode(CreditCardPortfolioCode.CHARGE);
                creditCardMainAccount.setCycleDay(4);
                creditCardMainAccount.setCreditCardProductName(CreditCardProductName.PLATINUMCARD);
                break;
            default:
                throw new IllegalArgumentException("Invalid portfolio code: " + portfolioCode);
        }
    }

    /**
     * Change the String value of activationFlag ("Y" or "N") into a Boolean
     */
    public static Boolean isActivated(String activationFlag) {
        switch (activationFlag) {
            case "Y" :
                return true;
            case "N" :
                return false;
            default:
                throw new IllegalArgumentException("Invalid activation flag: " + activationFlag);
        }
    }

    /**
     * Determine if account type if position (else regular)
     */
    public static CreditCardAccountType getCreditcardAccountType(String accountType) {
        switch (accountType) {
            case "PC1" :
            case "PC2" :
            case "PG1" :
            case "PG2" :
                return CreditCardAccountType.REGULAR;
            case "MB1" :
            case "MB2" :
            case "MD1" :
            case "MD2" :
                return CreditCardAccountType.MEMBER;
            case "PB1" :
            case "PB2" :
            case "PD1" :
            case "PD2" :
                return CreditCardAccountType.POSITION;
            default:
                throw new IllegalArgumentException("Invalid account type: " + accountType);
        }
    }

}
