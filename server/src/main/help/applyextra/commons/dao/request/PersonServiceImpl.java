package applyextra.commons.dao.request;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.model.Person;
import applyextra.commons.service.LoansService;
import applyextra.commons.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Lazy
@Slf4j
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Resource
    private LoansService loansService;

    private int expireTimeout = 60; // FIXME read from jndi properties

    @Override
    public List<Person> getPersons(final String requestId) {
        if (requestId == null) {
            log.warn("Cannot find persons for a null requestId");
            return new ArrayList<>();
        }
        return personRepository.findPersonsByRequestId(requestId);
    }

    @Override
    public Person getPerson(final String personId) {
        if (personId == null) {
            log.warn("Cannot find persons for a null partyId");
            return null;
        }
        final List<Person> result = personRepository.findPersonsByPartyId(personId);
        if (result.isEmpty()) {
            log.warn("Could not find person for "+personId);
            return null;
        } else if (result.size() > 1) {
            log.warn("Found more than one person for "+personId+". Returning the first.");
        }
        return result.get(0);
    }

    @Override
    @Transactional
    public Person updatePerson(final Person person) {
        if(person.getId() == null || person.getId().isEmpty()) {
            person.setId(UUID.randomUUID().toString());
        }
        person.setLastUpdated(new Date());
        return personRepository.saveAndFlush(person);
    }

    @Override
    @Transactional
    public void deletePerson(final Person person) {
        loansService.deleteLoansForPerson(person);
        personRepository.delete(person);
    }

    @Override
    @Transactional
    public void deletePersons(final String requestId) {
        final List<Person> personList = getPersons(requestId);
        for (Person person : personList) {
            deletePerson(person);
        }
    }

    @Override
    public int getExpireInterval() {
        return expireTimeout;
    }

}
