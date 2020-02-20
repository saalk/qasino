package applyextra.commons.model.financialdata;

public enum CardType {
    Creditcard(70),
    Platinumcard(71),
    Studentencard(73);

    private Integer key;

    CardType(Integer key) {
        this.key = key;
    }

    public Integer getKey() {
        return this.key;
    }
}
