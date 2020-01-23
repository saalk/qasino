package applyextra.commons.model;

import lombok.Getter;

public enum CreditScoreResult {
    RED("Red",10),
    GREEN("Green", 100),
    ORANGE("Orange", 50),
    MISSING("Missing",0 );

    private final String code;
    @Getter
    private final Integer successRate;

    CreditScoreResult(String code, int successRate) {
        this.code = code;
        this.successRate = successRate;
    }

    public String toString() { return this.code;}

    public static CreditScoreResult fromColor(String color){
    	return CreditScoreResult.valueOf(color.toUpperCase());
    }
}
