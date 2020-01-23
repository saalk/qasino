package applyextra.commons.dao.request;

import applyextra.commons.model.database.entity.PartyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyEntityRepository extends JpaRepository<PartyEntity, String> {
    PartyEntity findByPartyId(String partyId);
}
