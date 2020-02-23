package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.CreditScoreResult;
import applyextra.commons.model.database.entity.DecisionScoreEntity;
import applyextra.commons.model.financialdata.CardType;
import applyextra.commons.model.financialdata.dto.FinancialAcceptanceDTO;
import applyextra.commons.orchestration.Action;
import applyextra.commons.orchestration.DecisionScoreType;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class DetermineOverallDecisionScoreAction implements Action<FinancialAcceptanceDTO, EventOutput.Result> {

    @Override
    public EventOutput.Result perform(final FinancialAcceptanceDTO dto) {
        List<DecisionScoreEntity> decisionScores = dto.getPartyEntity().getDecisionScoreEntities();
        if (decisionScores.isEmpty()) {
            throw new IllegalArgumentException("Could not find decision scores to determine overall scoring");
        }
        decisionScores.sort(new ScoringComparator());
        final DecisionScoreEntity overallResult = decisionScores.get(0);
        dto.setOverallDecisionScore(overallResult);
        /*
        The first if is for (apply) studentcard, creditscore is checked but not taken for the final result
         */
        if (CardType.Studentencard.equals(dto.getCardType())
                && CreditScoreResult.RED.equals(overallResult.getResult())
            && DecisionScoreType.CREDITSCORE.equals(overallResult.getType())) {
            return EventOutput.Result.SUCCESS;
        } else if (!CreditScoreResult.GREEN.equals(overallResult.getResult())) {
            log.warn("Request with Id "+dto.getRequestId()+ " was declined in Scoring: "+decisionScores.get(0).getType().toString()+" is "+ decisionScores.get(0).getResult().toString());
            return EventOutput.Result.FAILURE;
        } else {
            return EventOutput.Result.SUCCESS;
        }
    }


    class ScoringComparator implements Comparator<DecisionScoreEntity> {

        @Override
        public int compare(DecisionScoreEntity thisObject, DecisionScoreEntity thatObject) {

            final Integer thisSuccessRate = thisObject.getResult().getSuccessRate();
            final Integer thatSuccessRate = thatObject.getResult().getSuccessRate();
            int compareResult = thisSuccessRate.compareTo(thatSuccessRate);

            if (compareResult == 0) {
                final Integer thisPriority = thisObject.getType().getPriority();
                final Integer thatPriority = thatObject.getType().getPriority();

                compareResult = thisPriority.compareTo(thatPriority);
            }
            return compareResult;
        }
    }

}