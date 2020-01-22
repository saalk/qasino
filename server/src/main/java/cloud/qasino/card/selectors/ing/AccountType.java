package cloud.qasino.card.selectors.ing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountType {

    POSITION("POSITION"),
    REGULAR("REGULAR"),
    MEMBER("MEMBER");

    @Getter
    private String accountType;

    public static AccountType string2Code(final String codeStr) {
        for (AccountType code : AccountType.values()) {
            if (code.accountType.equals(codeStr)) {
                return code;
            }
        }
        return null;
    }

    boolean isFamily() {
        return (!(this.name() == "REGULAR"));
    }
}


