package applyextra.commons.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountCategory {

    POSITION("POSITION"),
    REGULAR("REGULAR"),
    MEMBER("MEMBER");

    @Getter
    private String accountCategory;

    public static AccountCategory string2Code(final String codeStr) {
        for (AccountCategory code : AccountCategory.values()) {
            if (code.equals(codeStr)) {
                return code;
            }
        }
        return null;
    }

    boolean isFamily() { return (!("REGULAR".equals(this.name()))); }
}
