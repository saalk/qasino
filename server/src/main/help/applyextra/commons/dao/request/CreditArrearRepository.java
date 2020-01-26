package applyextra.commons.dao.request;

import applyextra.commons.model.CreditArrear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditArrearRepository extends JpaRepository<CreditArrear, String> {

    List<CreditArrear> findCreditArrearByExistingLoanId(final String existingLoanId);

}
