package applyextra.commons.dao.request;

import applyextra.commons.model.DecisionScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface DecisionScoreRepository extends JpaRepository<DecisionScore, String> {

    List<DecisionScore> findDecisionScoreByPersonIdAndLastUpdatedGreaterThan(final String personId, final Date daysSince);

}
