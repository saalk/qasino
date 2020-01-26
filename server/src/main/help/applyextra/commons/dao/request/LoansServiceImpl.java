package applyextra.commons.dao.request;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.model.CreditArrear;
import applyextra.commons.model.ExistingLoanAgreement;
import applyextra.commons.model.Loan;
import applyextra.commons.model.Person;
import applyextra.commons.service.LoansService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Lazy
@Slf4j
public class LoansServiceImpl implements LoansService {

    @Resource
    private LoanRepository loanRepository;

    @Resource
    private ExistingLoanAgreementRepository existingLoanAgreementRepository;

    @Resource
    private CreditArrearRepository creditArrearRepository;
    private int expireInterval = 60; // FIXME read from jndi

    @Override
    public List<ExistingLoanAgreement> getExistingLoans(final String personId) {
        List<ExistingLoanAgreement> loanAgreements = existingLoanAgreementRepository.findExistingLoanByPersonId(personId);
        for (ExistingLoanAgreement loanAgreement : loanAgreements) {
            final List<CreditArrear> loanArrears = creditArrearRepository.findCreditArrearByExistingLoanId(loanAgreement.getId());
            if (loanArrears != null) {
                loanAgreement.setCreditAgreementArrears(loanArrears);
            }
        }
        return loanAgreements;
    }

    @Override
    public List<Loan> getExtraLoans(final String personId) {
        return loanRepository.findLoanByPersonId(personId);
    }

    @Override
    @Transactional
    public void updateExistingLoans(final List<ExistingLoanAgreement> existingLoanAgreements) {
        for (ExistingLoanAgreement loanAgreement : existingLoanAgreements) {
            if (loanAgreement.getId() == null) {
                loanAgreement.setId(UUID.randomUUID().toString());
            }
        }
        existingLoanAgreementRepository.save(existingLoanAgreements);
        for (ExistingLoanAgreement loanAgreement : existingLoanAgreements) {
            saveArrears(loanAgreement);
        }
        existingLoanAgreementRepository.flush();
    }

    private void saveArrears(final ExistingLoanAgreement loanAgreement) {
        for (CreditArrear arrear : loanAgreement.getCreditAgreementArrears()) {
            if (arrear.getId() == null) {
                arrear.setId(UUID.randomUUID().toString());
            }
            arrear.setExistingLoanId(loanAgreement.getId());
        }
        creditArrearRepository.save(loanAgreement.getCreditAgreementArrears());
    }

    @Override
    @Transactional
    public void updateExtraLoans(final List<Loan> loans) {
        for(Loan loan : loans) {
            if(loan.getId() == null || loan.getId().isEmpty()) {
                loan.setId(UUID.randomUUID().toString());
            }
        }
        loanRepository.save(loans);
        loanRepository.flush();
    }

    @Override
    @Transactional
    public void cleanExpiredExtraLoans() {
        List<Loan> loans = loanRepository.findLoanByExpireTimeAfter(new Date());
        loanRepository.deleteInBatch(loans);
        loanRepository.flush();
    }

    @Override
    @Transactional
    public void cleanExpiredExistingLoans() {
        doDeleteExistingLoans(existingLoanAgreementRepository.findExistingLoanByExpireTimeAfter(new Date()));
        creditArrearRepository.flush();
        existingLoanAgreementRepository.flush();
    }

    private void doDeleteExistingLoans(final List<ExistingLoanAgreement> loans) {
        for (ExistingLoanAgreement loan : loans) {
            try {
                List<CreditArrear> arrears = creditArrearRepository.findCreditArrearByExistingLoanId(loan.getId());
                creditArrearRepository.deleteInBatch(arrears);
                existingLoanAgreementRepository.delete(loan);
            } catch (final Exception e) {
                log.warn("Failed to remove loan with id " + loan.getId() + ". Will try again on the next pass.");
            }
        }
    }

    @Override
    @Transactional
    public void deleteLoansForPerson(final Person person) {
        doDeleteExistingLoans(existingLoanAgreementRepository.findExistingLoanByPersonId(person.getId()));
        loanRepository.deleteInBatch(loanRepository.findLoanByPersonId(person.getId()));
        loanRepository.flush();
        creditArrearRepository.flush();
        existingLoanAgreementRepository.flush();
    }

    @Override
    public int getExpireInterval() {
        return expireInterval;
    }

}
