package applyextra.commons.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RepricingProgram {

    CREDITCARD_CHARGE_MEMBER_ANYLIMIT("RNWEBF"),
    CREDITCARD_CHARGE_POSITION_ANYLIMIT("RNWEBF"),
    CREDITCARD_CHARGE_REGULAR_ANYLIMIT("RFUL01"),

    CREDITCARD_REVOLVING_MEMBER_ANYLIMIT("RNWEBR"),
    CREDITCARD_REVOLVING_MEMBER_HIGH("RINTGD"),
    CREDITCARD_REVOLVING_MEMBER_LOW("RINTBL"),

    CREDITCARD_REVOLVING_POSITION_ANYLIMIT("RNWEBR"),
    CREDITCARD_REVOLVING_POSITION_HIGH("RINTGD"),
    CREDITCARD_REVOLVING_POSITION_LOW("RINTBL"),

    CREDITCARD_REVOLVING_REGULAR_ANYLIMIT("RREV01"),
    CREDITCARD_REVOLVING_REGULAR_HIGH("RINT07"),
    CREDITCARD_REVOLVING_REGULAR_LOW("RINT01"),

    PLATINUMCARD_CHARGE_MEMBER_ANYLIMIT("RNWEBF"),
    PLATINUMCARD_CHARGE_POSITION_ANYLIMIT("RNWEBF"),
    PLATINUMCARD_CHARGE_REGULAR_ANYLIMIT("RFUL01"),

    PLATINUMCARD_REVOLVING_MEMBER_ANYLIMIT("RNWEGR"),
    PLATINUMCARD_REVOLVING_MEMBER_HIGH("RINTGD"),
    PLATINUMCARD_REVOLVING_MEMBER_LOW("RINTBL"),

    PLATINUMCARD_REVOLVING_POSITION_ANYLIMIT("RNWEGR"),
    PLATINUMCARD_REVOLVING_POSITION_HIGH("RINTGD"),
    PLATINUMCARD_REVOLVING_POSITION_LOW("RINTBL"),

    PLATINUMCARD_REVOLVING_REGULAR_ANYLIMIT("RREV02"),
    PLATINUMCARD_REVOLVING_REGULAR_HIGH("RINT07"),
    PLATINUMCARD_REVOLVING_REGULAR_LOW("RINT01"),

    //TODO remove when no longer uses due to RepricingProgramSelector
    CHARGE_REGULAR("RFUL01"),
    CHARGE_POSITION("RNWEBF"),
    REGULAR_PBR_LESS("RINT01"),
    REGULAR_PBR_GREATER("RINT07"),
    REGULAR_PGR_LESS("RINT01"),
    REGULAR_PGR_GREATER("RINT07"),
    POSITION_PBR_LESS("RINTBL"),
    POSITION_PBR_GREATER("RINTGD"),
    POSITION_PGR_LESS("RINTBL"),
    POSITION_PGR_GREATER("RINTGD"),

    CREDITCARD_REVOLVING_FREE_CARD("FMBX00"),
    CREDITCARD_REVOLVING_FREE_EXTRA_CARD("FMBX00"),
    CREDITCARD_CHARGE_FREE_CARD("FMBX00"),
    CREDITCARD_CHARGE_FREE_EXTRA_CARD("FMBX00"),
    PLATINUMCARD_REVOLVING_FREE_CARD("FMBX00"),
    PLATINUMCARD_REVOLVING_FREE_EXTRA_CARD("FMBX00"),
    PLATINUMCARD_CHARGE_FREE_CARD("FMBX00"),
    PLATINUMCARD_CHARGE_FREE_EXTRA_CARD("FMBX00");


    @Getter
    private String repricingProgram;

    public static RepricingProgram string2Code(final String codeStr) {
        for (RepricingProgram code : RepricingProgram.values()) {
            if (code.repricingProgram.equals(codeStr)) {
                return code;
            }
        }
        return null;
    }
}