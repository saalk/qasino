package applyextra;

import java.util.Collection;
import java.util.Optional;

public interface Customer extends Validatable {
    String getContextReference();

    String getCustomerId();

    Customer.CustomerQualification getCustomerQualification();

    Optional<String> getContract();

    Optional<String> getOrganisation();

    Optional<String> getAccessMeansAgreementId();

    Optional<String> getAuthorizationMeansAgreementId();

    Integer getAuthenticationLevel();

    Collection<String> getPilotGroups();

    Optional<Long> getSessionStart();

    Optional<Long> getSessionEnd();

    public static enum CustomerQualification {
        RETAIL,
        BUSINESS;

        private CustomerQualification() {
        }
    }
}
