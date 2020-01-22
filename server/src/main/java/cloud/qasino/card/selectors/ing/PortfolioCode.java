package cloud.qasino.card.selectors.ing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PortfolioCode {
    CREDITCARD_REVOLVING("PBR"),
    CREDITCARD_CHARGE("PBC"),
    PLATINUMCARD_REVOLVING("PGR"),
    PLATINUMCARD_CHARGE("PGC");

    @Getter
    private String portfolioCode;

    public static PortfolioCode string2Code(final String codeStr) {
        for (PortfolioCode code : PortfolioCode.values()) {
            if (code.portfolioCode.equals(codeStr)) {
                return code;
            }
        }
        return null;
    }

    public static boolean isRevolving(PortfolioCode portfolioCode) {
        return (portfolioCode.equals(CREDITCARD_REVOLVING) || portfolioCode.equals(PLATINUMCARD_REVOLVING));
    }

    public static boolean isCharge(PortfolioCode portfolioCode) {
        return !isRevolving(portfolioCode);
    }

    public static boolean isCreditcard(PortfolioCode portfolioCode) {
        return (portfolioCode.equals(PortfolioCode.CREDITCARD_REVOLVING) || portfolioCode.equals(CREDITCARD_CHARGE));
    }

    public static boolean isPlatinumcard(PortfolioCode portfolioCode) {
        return !isCreditcard(portfolioCode);
    }
}
