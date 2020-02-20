package applyextra.commons.dao.request;

import applyextra.commons.model.database.entity.ExistingLoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExistingLoanEntityRepository extends JpaRepository<ExistingLoanEntity, String> {
    List<ExistingLoanEntity> findByPartyIdAndRequestId(String partyId, String requestId);
}
