package applyextra.operations.event;

import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.Person;
import applyextra.commons.model.financialdata.FinancialData;
import applyextra.commons.service.DecisionScoreService;
import applyextra.commons.service.PersonService;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.value.CreditScoreResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Lazy
public class GetPersonDetailsEvent extends AbstractEvent {

    @Resource
    private PersonService personService;

    @Resource
    private DecisionScoreService decisionScoreService;

    @Override
    protected EventOutput execution(final Object... eventOutput) {
        GetPersonDetailsEventDTO flowDTO = (GetPersonDetailsEventDTO) eventOutput[0];
        FinancialData financialData = flowDTO.getFinancialData();
        List<Person> persons = personService.getPersons(flowDTO.getRequestId());
        if (persons.isEmpty()) {
            financialData.getRequester().setRequestId(flowDTO.getRequestId());
            createPersons(financialData);
        } else {
            readPersons(financialData, persons);
        }
        flowDTO.setCreditScoreResult(decisionScoreService.getCreditScoreResults(financialData.getRequester().getPartyId()));
        return EventOutput.success();
    }

    private Person updateInformation(final Person personFromDB, final Person person) {
        if (person == null) {
            return personFromDB;
        }
        boolean updated = false;
        if (person.getSourceOfIncome() != null) { // source has more information that the DB
            personFromDB.copyFinancialInformation(person);
            updated = true;
        }
        if (person.getPartyId() != null || person.getLastName() != null) { // source has more information that the DB
            personFromDB.copyIndividualInformation(person);
            updated = true;
        }
        if (updated) {
            personService.updatePerson(personFromDB);
        }
        return personFromDB;
    }

    private void readPersons(final FinancialData financialData, final List<Person> persons) {
        financialData.setRequester(updateInformation(persons.get(0), financialData.getRequester()));
    }

    private void createPersons(final FinancialData financialData) {
        personService.updatePerson(financialData.getRequester());
    }

    public interface GetPersonDetailsEventDTO {
        String getRequestId();

        String getRequestorId();

        FinancialData getFinancialData();

        void setCreditScoreResult(CreditScoreResult result);
    }
}
