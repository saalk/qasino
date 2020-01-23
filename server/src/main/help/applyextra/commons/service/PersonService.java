package applyextra.commons.service;

import applyextra.commons.model.Person;

import java.util.List;

/**
 * This service provides a persistent storage for person information for the financial acceptance. This information should be
 * removed after the request is fulfilled or cancelled.
 */
public interface PersonService {

    /**
     * Get a list of persons for a certain request id
     */
    List<Person> getPersons(final String requestId);

    /**
     * Retrieve a person, when the partyId is known (Please beware: Does not include the pin code of their card. Contact the
     * person in person for that.)
     */
    Person getPerson(final String partyId);

    /**
     * Update a single person record
     *
     * @param person person record to be updated
     * @return updated person record
     */
    Person updatePerson(final Person person);

    /**
     * A Dalek flies in and suckers the person from existence
     */
    void deletePerson(final Person person);

    /**
     * Activates a squad of Cybermen for maximum deletion.
     */
    void deletePersons(final String requestId);

    /**
     * Return the maximum lifetime interval of the personal data
     *
     * @return Lifetime interval in minutes
     */
    int getExpireInterval();

}
