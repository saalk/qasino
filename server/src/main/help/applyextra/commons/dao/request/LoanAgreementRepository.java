package applyextra.commons.dao.request;

import applyextra.commons.model.database.entity.CreditCardLoanAgreementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanAgreementRepository extends JpaRepository<CreditCardLoanAgreementEntity, String> {

  CreditCardLoanAgreementEntity findFirstByMainAgreementOrderByUpdateTimeDesc(final String mainAgreement);
  List<CreditCardLoanAgreementEntity> findAllByMainAgreementOrderByUpdateTimeDesc(final String mainAgreement);
  List<CreditCardLoanAgreementEntity> findAllByCreditCardNumberAndLoanAgreementStatusAndAgreementStatusOrderByUpdateTimeDesc(
          final String creditCardNumber, final CreditCardLoanAgreementEntity.Status loanAgreementStatus, final CreditCardLoanAgreementEntity.Status agreementStatus);
  List<CreditCardLoanAgreementEntity> findAllByRgbOrderByUpdateTimeDesc(final String rgb);
  List<CreditCardLoanAgreementEntity> findAllByCreditCardNumberAndLoanAgreementStatusOrderByUpdateTimeDesc(final String creditcardNumber, final CreditCardLoanAgreementEntity.Status loanAgreementStatus);
}
