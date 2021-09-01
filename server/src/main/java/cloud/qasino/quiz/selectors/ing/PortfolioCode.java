package cloud.qasino.quiz.selectors.ing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PortfolioCode {
    CREDITQUIZ_REVOLVING("PBR"),
    CREDITQUIZ_CHARGE("PBC"),
    PLATINUMQUIZ_REVOLVING("PGR"),
    PLATINUMQUIZ_CHARGE("PGC");

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
        return (portfolioCode.equals(CREDITQUIZ_REVOLVING) || portfolioCode.equals(PLATINUMQUIZ_REVOLVING));
    }

    public static boolean isCharge(PortfolioCode portfolioCode) {
        return !isRevolving(portfolioCode);
    }

    public static boolean isCreditquiz(PortfolioCode portfolioCode) {
        return (portfolioCode.equals(PortfolioCode.CREDITQUIZ_REVOLVING) || portfolioCode.equals(CREDITQUIZ_CHARGE));
    }

    public static boolean isPlatinumquiz(PortfolioCode portfolioCode) {
        return !isCreditquiz(portfolioCode);
    }
}
