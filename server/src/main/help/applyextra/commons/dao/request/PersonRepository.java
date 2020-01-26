package applyextra.commons.dao.request;

import applyextra.commons.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, String> {

    List<Person> findPersonsByRequestId(final String requestId);

    List<Person> findPersonsByPartyId(final String partyId);

}
