package applyextra.commons.model.financialdata.transformer;

import applyextra.commons.model.Loan;
import applyextra.commons.model.Person;
import applyextra.commons.model.PortfolioCode;
import applyextra.commons.model.SourceOfIncome;
import applyextra.commons.model.financialdata.HousingCostsType;
import applyextra.commons.model.financialdata.MaritalStatus;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class FinancialDataTransformer {

    public static nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.value.Partner transformScore(final Person person) {
        if (person == null) {
            return null;
        }
        final nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.value.Partner result = new nl.ing.sc
                .creditcardmanagement.checkcreditcardcreditscore1.value.Partner();
        result.setInitials(person.getInitials());
        result.setPartyId(person.getPartyId());
        result.setLastName(person.getLastName());
        if (person.getBirthDate() != null) {
            result.setBirthDate(new LocalDate(person.getBirthDate()));
        }
        result.setPrefix(person.getPrefix());
        return result;
    }

    public static nl.ing.sc.creditcardmanagement.checkrequestedcreditcardlimit1.value.Partner transformLimit(final Person person) {
        if (person == null) {
            return null;
        }
        final nl.ing.sc.creditcardmanagement.checkrequestedcreditcardlimit1.value.Partner result = new nl.ing.sc
                .creditcardmanagement.checkrequestedcreditcardlimit1.value.Partner();
        if (person.getBirthDate() != null) {
            result.setBirthDate(new LocalDate(person.getBirthDate()));
        }
        result.setPartyId(person.getPartyId());
        result.setSourceOfIncome(transformSourceOfIncome(person.getSourceOfIncome()));
        result.setMonthlyNetIncome(person.getMonthlyNetIncome());
        result.setMonthlyAlimony(person.getMonthlyAlimony());
        result.setChildrenPresent(person.getChildrenPresent());
        result.setIncomeFullLastYear(person.getIncomeFullLastYear());
        return result;
    }

    public static nl.ing.sc.creditcardmanagement.commonobjects.SourceOfIncome transformSourceOfIncome(final SourceOfIncome sourceOfIncome) {
        if (sourceOfIncome == null) {
            return null;
        }
        return nl.ing.sc.creditcardmanagement.commonobjects.SourceOfIncome.fromCode(sourceOfIncome.getCode());
    }

    public static nl.ing.sc.creditcardmanagement.commonobjects.PortfolioCode transformPortfolioCode(final PortfolioCode portfolioCode) {
        if (PortfolioCode.CREDITCARD_CHARGE.equals(portfolioCode) || PortfolioCode.PLATINUMCARD_CHARGE.equals(portfolioCode)) {
            return nl.ing.sc.creditcardmanagement.commonobjects.PortfolioCode.CHARGE;
        } else if (PortfolioCode.CREDITCARD_REVOLVING.equals(portfolioCode) || PortfolioCode.PLATINUMCARD_REVOLVING.equals
                (portfolioCode)) {
            return nl.ing.sc.creditcardmanagement.commonobjects.PortfolioCode.REVOLVING;
        }
        return null;
    }

    public static nl.ing.sc.creditcardmanagement.commonobjects.MaritalStatus transformMaritalStatus(final MaritalStatus maritalStatus) {
		if(maritalStatus == null){
			return null;
		}
		return nl.ing.sc.creditcardmanagement.commonobjects.MaritalStatus.fromCode(maritalStatus.getCode());
    }

    public static nl.ing.sc.creditcardmanagement.commonobjects.HousingCostsType transformHousingCostsType(final HousingCostsType housingCostsType) {
		if(housingCostsType == null) {
			return null;
		}
		return nl.ing.sc.creditcardmanagement.commonobjects.HousingCostsType.fromCode(housingCostsType.getCode());
    }

    public static List<nl.ing.sc.creditcardmanagement.commonobjects.Loan> transformLoans(final List<Loan> loans, final Person person) {
        if (loans == null) {
            return null;
        }
        final List<nl.ing.sc.creditcardmanagement.commonobjects.Loan> resultLoans = new ArrayList<>();
        for (Loan loan : loans) {
            final nl.ing.sc.creditcardmanagement.commonobjects.Loan resultLoan =
                    new nl.ing.sc.creditcardmanagement.commonobjects.Loan(
                            loan.getTotalAmount(),
                            loan.getMonthlyAmount(),
                            loan.getCounterParty()
                    );
            resultLoans.add(resultLoan);
            loan.setPersonId(person.getId());
        }

        return resultLoans;
    }

}
