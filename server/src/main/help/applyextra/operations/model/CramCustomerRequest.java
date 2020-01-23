package applyextra.operations.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class CramCustomerRequest {
    @NotNull
    @JsonProperty("CustomerRequest")
    private CustomerRequest customerRequest;

    @Getter
    @Setter
    public static class CustomerRequest {
        @NotNull
        @JsonProperty("ExternalReference")
        private ExternalReference externalReference;
        @NotNull
        @Pattern(regexp = "[0-9]+")
        @JsonProperty("ReferenceId")
        private String referenceId;
        @NotNull
        @Pattern(regexp = "[0-9]+")
        @JsonProperty("PartyId")
        private String partyId;
        @NotNull
        @Pattern(regexp = "[0-9]+")
        @JsonProperty("Signatures")
        private String signatures;
        @NotNull
        @JsonProperty("Status")
        private Status status;
        @NotNull
        @JsonProperty("Authorisation")
        private Authorisation authorisation;
    }

    @Getter
    @Setter
    public static class ExternalReference {
        @NotNull
        @JsonProperty("Id")
        @Pattern(regexp = "[0-9A-Za-z]+")
        private String id;
        @NotNull
        @JsonProperty("Origin")
        @Pattern(regexp = "[A-Za-z]+")
        private String origin;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties("DocumentReference")
    public static class Authorisation {
        @NotNull
        @Pattern(regexp = "[0-9]+")
        @JsonProperty("PartyId")
        private String partyId;
        @NotNull
        @Pattern(regexp = "[A-Za-z]+")
        @JsonProperty("Method")
        private String method;
        @NotNull
        @JsonProperty("Status")
        private AuthorisationStatus status;
        @NotNull
        @Pattern(regexp = "[0-9]+")
        @JsonProperty("Role")
        private String role;
    }

    public enum Status {
        // @formatter:off
        Initiated("Initiated"),
        Draft("Draft"),
        Submitted("Submitted"),
        Authorised("Authorised"),
        Execution("Execution"),
        Fulfilled("Fulfilled"),
        Declined("Declined"),
        Expired("Expired"),
        Rejected("Rejected"),
        Cancelled("Cancelled");
        // @formatter:on

        @Getter
        private final String status;

        Status(String status) {
            this.status = status;
        }
    }

    public enum AuthorisationStatus {
        // @formatter:off
        Initiated("Initiated"),
        Draft("Draft"),
        Submitted("Submitted"),
        Authorised("Authorised"),
        Execution("Execution"),
        Fulfilled("Fulfilled"),
        Declined("Declined"),
        Expired("Expired"),
        Rejected("Rejected"),
        Cancelled("Cancelled");
        // @formatter:on

        @Getter
        private final String authorizationStatus;

        AuthorisationStatus(String status) {
            this.authorizationStatus = status;
        }
    }
}
