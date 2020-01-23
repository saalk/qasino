package applyextra.commons.service;

import applyextra.commons.model.DecisionScore;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.value.CreditScoreResult;

import java.util.List;

public interface DecisionScoreService {

    List<DecisionScore> updateDecisionScore(List<DecisionScore> subScores);

    CreditScoreResult getCreditScoreResults(String partyId);

}
