package applyextra.commons.dao.request;

import lombok.Getter;
import applyextra.commons.model.DecisionScore;
import applyextra.commons.service.DecisionScoreService;
import nl.ing.riaf.core.util.JNDIUtil;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.value.CreditScoreResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Lazy
public class DecisionScoreServiceImpl implements DecisionScoreService {
    
    @Getter
    private int daysSince; //NOSONAR
    
    @Resource
    private JNDIUtil util;  
    
    @Resource
    private DecisionScoreRepository scoreRepository;

    @PostConstruct
    public void init() {
        final Integer jndiValue = util.getJndiIntValue("param/creditscoreresult/dayssince", true);
        this.daysSince = jndiValue;
    }

    @Override
    public List<DecisionScore> updateDecisionScore(List<DecisionScore> subScores) {
        return scoreRepository.save(subScores);
    }

    @Override
    public CreditScoreResult getCreditScoreResults(String partyId) {
        final List<DecisionScore> decisionScores = scoreRepository
                .findDecisionScoreByPersonIdAndLastUpdatedGreaterThan(partyId, getEarlierDate(daysSince));
        return getOverallDecisionScore(decisionScores);
    }

    /**
     * gets the overall decision score after getting all score checks from database
     *
     * @param decisionScores
     * @return CreditScoreResult
     */
    public static CreditScoreResult getOverallDecisionScore(final List<DecisionScore> decisionScores) {

        if (decisionScores == null) {
            throw new IllegalArgumentException("call to getOverallDecisionScore contains null");
        }

        List<CreditScoreResult> results = new ArrayList<>();
        for (DecisionScore decisionScore : decisionScores) {
            results.add(decisionScore.getResult());
        }

        if (results.contains(CreditScoreResult.MISSING)) {
            return CreditScoreResult.MISSING;
        } else if (results.contains(CreditScoreResult.RED)) {
            return CreditScoreResult.RED;
        } else if (results.contains(CreditScoreResult.ORANGE)) {
            return CreditScoreResult.ORANGE;
        } else if (results.contains(CreditScoreResult.GREEN)) {
            return CreditScoreResult.GREEN;
        }
        return CreditScoreResult.MISSING;
    }

    /**
     * we are using greater than function of spring data JPA and applying it on yesterdays date
     * 
     * @return date
     * @param daysSince
     */
    private Date getEarlierDate(final int daysSince) {
        return new Date(System.currentTimeMillis() - 1000L * daysSince * 24 * 60 * 60);
    }
    
}
