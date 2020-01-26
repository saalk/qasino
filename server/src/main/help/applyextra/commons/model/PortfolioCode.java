package applyextra.commons.model;

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
}
