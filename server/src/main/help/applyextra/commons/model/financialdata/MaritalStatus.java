package applyextra.commons.model.financialdata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum MaritalStatus {
    SINGLE("001"),
    LIVING_TOGETHER("002"),
    MARRIED("003"),
    DIVORCED("034");

    private static final Map<String, MaritalStatus> lookup;
    private final String code;

    private MaritalStatus(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return this.code;
    }

    public static MaritalStatus fromCode(String code) {
        return (MaritalStatus)lookup.get(code);
    }

    @JsonCreator
    public static MaritalStatus newInstance(String code) {
        return fromCode(code);
    }

    static {
        lookup = new HashMap();
        MaritalStatus[] arr$ = values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            MaritalStatus maritalStatus = arr$[i$];
            lookup.put(maritalStatus.code, maritalStatus);
        }

    }
}
