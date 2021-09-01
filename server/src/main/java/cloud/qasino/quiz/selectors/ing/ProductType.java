package cloud.qasino.quiz.selectors.ing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductType {

    PLATINUMQUIZ("Platinumquiz"),
    CREDITQUIZ("Creditquiz");

    @Getter
    private String productType;

    public static ProductType string2Code(final String codeStr) {
        for (ProductType code : ProductType.values()) {
            if (code.productType.equals(codeStr)) {
                return code;
            }
        }
        return null;
    }

}