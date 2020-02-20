package applyextra.commons.dao.request;

import applyextra.commons.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, String> {

    List<Loan> findLoanByPersonId(final String personId);

    List<Loan> findLoanByExpireTimeAfter(final Date dataExpireTime);

}
