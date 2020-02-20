package applyextra.commons.orchestration;

import lombok.Getter;

public enum DecisionScoreType {
    LIMITCHECK(10),
    BKRSCORE(20),
    CREDITSCORE(30);


    @Getter
    private final Integer priority;

    DecisionScoreType(int priorityCode){
        this.priority = priorityCode;
    }


}
