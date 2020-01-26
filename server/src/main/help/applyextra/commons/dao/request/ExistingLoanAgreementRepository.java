package applyextra.commons.dao.request;

import applyextra.commons.model.ExistingLoanAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ExistingLoanAgreementRepository extends JpaRepository<ExistingLoanAgreement, String> {

    List<ExistingLoanAgreement> findExistingLoanByPersonId(final String personId);

    List<ExistingLoanAgreement> findExistingLoanByExpireTimeAfter(final Date dataExpireTime);

}
