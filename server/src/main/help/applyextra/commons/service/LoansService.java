package applyextra.commons.service;

import applyextra.commons.model.ExistingLoanAgreement;
import applyextra.commons.model.Loan;
import applyextra.commons.model.Person;

import java.util.List;

public interface LoansService {

    List<ExistingLoanAgreement> getExistingLoans(String personId);
    List<Loan> getExtraLoans(String personId);
    void updateExistingLoans(List<ExistingLoanAgreement> existingLoanAgreements);
    void updateExtraLoans(List<Loan> loans);
    void cleanExpiredExtraLoans();
    void cleanExpiredExistingLoans();
    void deleteLoansForPerson(Person person);

    /**
     * Return the maximum lifetime interval of the financial data
     *
     * @return Lifetime interval in minutes
     */
    int getExpireInterval();
}
