package cloud.qasino.card.selectors.ing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductType {

    PLATINUMCARD("Platinumcard"),
    CREDITCARD("Creditcard");

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