package applyextra.commons.dao.request;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.model.database.entity.CreditCardLoanAgreementEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional
@Lazy
public class LoanAgreementService {

    @Resource
    private LoanAgreementRepository loanAgreementRepository;

    @Transactional
    public CreditCardLoanAgreementEntity createCreditCardLoanAgreement(final CreditCardLoanAgreementEntity loanAgreement) {
        return loanAgreementRepository.save(loanAgreement);
    }

    @Transactional(readOnly = true)
    public List<CreditCardLoanAgreementEntity> getLoanAgreementByMainAgreement(final String mainAgreement) {
        return loanAgreementRepository.findAllByMainAgreementOrderByUpdateTimeDesc(mainAgreement);
    }

    @Transactional(readOnly = true)
    public List<CreditCardLoanAgreementEntity> getLoanAgreementByCreditCardNumber(final String creditCardNumber
            , final CreditCardLoanAgreementEntity.Status loanAgreementStatus
            , final CreditCardLoanAgreementEntity.Status agreementStatus) {
        return loanAgreementRepository.findAllByCreditCardNumberAndLoanAgreementStatusAndAgreementStatusOrderByUpdateTimeDesc(creditCardNumber, loanAgreementStatus, agreementStatus);
    }

    @Transactional(readOnly = true)
    public CreditCardLoanAgreementEntity getActiveLoanAgreementByCreditCardNumber(final String creditCardNumber) {
        List<CreditCardLoanAgreementEntity> loanAgreements = loanAgreementRepository.findAllByCreditCardNumberAndLoanAgreementStatusOrderByUpdateTimeDesc(creditCardNumber, CreditCardLoanAgreementEntity.Status.ACTIVE);
        if (loanAgreements.size() != 1) {
            throw new InvalidRequestException("Incorrect request for retrieving loan agreements, amount of active loan agreements is not one");
        }
        return loanAgreements.get(0);
    }

    @Transactional(readOnly = true)
    public List<CreditCardLoanAgreementEntity> getLoanAgreementByRgb(final String rgb) {
        return loanAgreementRepository.findAllByRgbOrderByUpdateTimeDesc(rgb);
    }

    @Transactional(readOnly = true)
    public CreditCardLoanAgreementEntity getLatestLoanAgreementByMainAgreement(final String mainAgreement) {
        return loanAgreementRepository.findFirstByMainAgreementOrderByUpdateTimeDesc(mainAgreement);
    }

}
