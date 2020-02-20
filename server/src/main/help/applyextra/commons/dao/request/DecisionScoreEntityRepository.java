package applyextra.commons.dao.request;

import applyextra.commons.model.database.entity.DecisionScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DecisionScoreEntityRepository extends JpaRepository<DecisionScoreEntity, String> {
    List<DecisionScoreEntity> findByPartyIdAndRequestId(String partyId, String requestId);
}
