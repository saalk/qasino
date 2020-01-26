package applyextra.commons.dao.request;

import applyextra.commons.model.database.entity.CreditCardRejectedRulesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RejectedRulesRepository extends JpaRepository<CreditCardRejectedRulesEntity, String> {
    List<CreditCardRejectedRulesEntity> findByRequest_Id(String requestId);
}
