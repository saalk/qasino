package applyextra.commons.model.financialdata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum HousingCostsType {
    HYPOTHEEK_VRIJ("021"),
    HYPOTHEEK_ING("022"),
    HYPOTHEEK_OVERIG("023"),
    HUUR("024"),
    KOSTGELD("025");

    private static final Map<String, HousingCostsType> lookup;
    private final String code;

    private HousingCostsType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return this.code;
    }

    public static HousingCostsType fromCode(String code) {
        return (HousingCostsType)lookup.get(code);
    }

    @JsonCreator
    public static HousingCostsType newInstance(String code) {
        return fromCode(code);
    }

    static {
        lookup = new HashMap();
        HousingCostsType[] arr$ = values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            HousingCostsType housingCostsType = arr$[i$];
            lookup.put(housingCostsType.code, housingCostsType);
        }

    }
}
