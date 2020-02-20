package applyextra.commons.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum SourceOfIncome {

    // Old FA version
    VAST_CONTRACT("006"),
    CONTRACT_BEPAALDE_TIJD("007"),
    DIRECTEUR_GROOTAANDEELHOUDER_BV("008"),
    EENMANSZAAK("009"),
    VENNOOT_V_O_F("010"),
    MAATSCHAP("011"),
    ZELFSTANDIGE_ZONDER_PERSONEEL_ZZP("012"),
    INKOMSTEN_UIT_VERHUUR_ONROEREND_GOED("013"),
    UITZENDKRACHT("014"),
    WERKLOOSHEID_WW_IOAW_IOAZ("015"),
    BIJSTAND_ABW_WWB("016"),
    ARBEIDSONGESCHIKT_WAO_WAZ_WAJONG("017"),
    AOW_ANW_PENSIOENUITKERING("018"),
    ANDERE_UITKERING("019"),
    GEEN_HUISVROUW_MAN("020"),
    // New FA version
    PERMANENT_CONTRACT("Permanent contract"),
    FIXED_TERM_CONTRACT("Fixed term contract"),
    TEMP_EMPLOYEE("Temporary employee"),
    ALLOWANCE("Allowance"),
    UNEMPLOYED("Unemployed");

    private static final Map<String, SourceOfIncome> lookup = new HashMap<>();
    static {
        for (final SourceOfIncome sourceOfIncome : SourceOfIncome.values()) {
            lookup.put(sourceOfIncome.code, sourceOfIncome);
        }
    }

    @Getter
    private final String code;

    SourceOfIncome(final String code) {
        this.code = code;
    }

    public static SourceOfIncome fromCode(String code) {
        return lookup.get(code);
    }

    public boolean isEntrepreneur() {
        return DIRECTEUR_GROOTAANDEELHOUDER_BV == this || EENMANSZAAK == this || VENNOOT_V_O_F == this || MAATSCHAP == this
                || ZELFSTANDIGE_ZONDER_PERSONEEL_ZZP == this || INKOMSTEN_UIT_VERHUUR_ONROEREND_GOED == this;
    }

    public boolean isWorker() {
        return UITZENDKRACHT == this || CONTRACT_BEPAALDE_TIJD == this || VAST_CONTRACT == this;
    }

    public boolean isTemporary() {
        return CONTRACT_BEPAALDE_TIJD == this || UITZENDKRACHT == this;
    }

}
