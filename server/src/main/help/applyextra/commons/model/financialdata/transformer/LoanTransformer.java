package applyextra.commons.model.financialdata.transformer;

import applyextra.commons.model.CreditArrear;
import applyextra.commons.model.ExistingLoanAgreement;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.jaxb.generated.CheckCreditCardCreditScore001Reply;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.jaxb.generated.CreditAgreementArrearType;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.jaxb.generated.TypeName;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoanTransformer {

    public static CheckCreditCardCreditScore001Reply.LoanAgreement loan2Agreement(final ExistingLoanAgreement source) {
        final CheckCreditCardCreditScore001Reply.LoanAgreement dest = new CheckCreditCardCreditScore001Reply.LoanAgreement();
        dest.setAgreementNumber(source.getAgreementNumber());
        dest.setAgreementType(source.getAgreementType());
        dest.setCreditLimit(source.getCreditLimit());
        dest.setFirstDownPaymentDate(new LocalDate(source.getFirstDownPaymentDate()));
        dest.setCalcFinalDownPaymentDate(new LocalDate(source.getCalcFinalDownPaymentDate()));
        dest.setRealFinalDownPaymentDate(new LocalDate(source.getRealFinalDownPaymentDate()));
        dest.setRegistrationDate(new LocalDate(source.getRegistrationDate()));
        dest.getCreditAgreementArrears()
                .addAll(arrear2ArrearType(source.getCreditAgreementArrears()));
        dest.setRemark(source.getRemark());
        dest.setHoldership(source.getHoldership());
        return dest;
    }

    private static List<CreditAgreementArrearType> arrear2ArrearType(final List<CreditArrear> creditAgreementArrears) {
        if (creditAgreementArrears == null) {
            return null;
        }
        final List<CreditAgreementArrearType> dest = new ArrayList<>();
        for (CreditArrear agreementArrearType : creditAgreementArrears) {
            final CreditAgreementArrearType e = new CreditAgreementArrearType();
            e.setStartOrChangeDateArrear(new LocalDate(agreementArrearType.getStartOrChangeDateArrear()));
            e.setTypeName(TypeName.fromValue(agreementArrearType.getType()));
            dest.add(e);
        }
        return dest;
    }

    public static ExistingLoanAgreement agreement2Loan(CheckCreditCardCreditScore001Reply.LoanAgreement source) {
        final ExistingLoanAgreement dest = new ExistingLoanAgreement();
        dest.setAgreementNumber(source.getAgreementNumber());
        dest.setAgreementType(source.getAgreementType());
        dest.setCreditLimit(source.getCreditLimit());
        dest.setFirstDownPaymentDate(source.getFirstDownPaymentDate() == null ? null : source.getFirstDownPaymentDate()
                .toDate());
        dest.setCalcFinalDownPaymentDate(source.getCalcFinalDownPaymentDate() == null ? null : source.getCalcFinalDownPaymentDate()
                .toDate());
        dest.setRealFinalDownPaymentDate(source.getRealFinalDownPaymentDate() == null ? null : source.getRealFinalDownPaymentDate()
                .toDate());
        dest.setRegistrationDate(source.getRegistrationDate() == null ? null : source.getRegistrationDate()
                .toDate());
        dest.setCreditAgreementArrears(arrearType2Arrear(source.getCreditAgreementArrears()));
        dest.setRemark(source.getRemark());
        dest.setHoldership(source.getHoldership());
        return dest;
    }

    private static List<CreditArrear> arrearType2Arrear(final List<CreditAgreementArrearType> creditAgreementArrears) {
        if (creditAgreementArrears == null) {
            return null;
        }
        final List<CreditArrear> result = new ArrayList<>();
        for (CreditAgreementArrearType agreementArrearType : creditAgreementArrears) {
            final CreditArrear e = new CreditArrear();
            e.setStartOrChangeDateArrear(agreementArrearType.getStartOrChangeDateArrear() == null ? null
                    : agreementArrearType.getStartOrChangeDateArrear()
                            .toDate());
            e.setType(agreementArrearType.getTypeName() == null ? null : agreementArrearType.getTypeName()
                    .toString());
            result.add(e);
        }
        return result;
    }

    public static List<CheckCreditCardCreditScore001Reply.LoanAgreement> loans2Agreements(
            final List<ExistingLoanAgreement> existingLoans) {
        if (existingLoans == null) {
            return null;
        }
        final List<CheckCreditCardCreditScore001Reply.LoanAgreement> result = new ArrayList<>();
        for (ExistingLoanAgreement loanAgreement : existingLoans) {
            result.add(loan2Agreement(loanAgreement));
        }
        return result;
    }

    public static List<ExistingLoanAgreement> agreements2Loans(
            final List<CheckCreditCardCreditScore001Reply.LoanAgreement> loanAgreements) {

        if (loanAgreements != null) {
            final List<ExistingLoanAgreement> result = new ArrayList<>();
            for (CheckCreditCardCreditScore001Reply.LoanAgreement loanAgreement : loanAgreements) {
                result.add(agreement2Loan(loanAgreement));
            }
            return result;
        }

        return Collections.emptyList();

    }
}
