package applyextra.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalarySlipDecision {

    //TODO improve type safety
    private String caseId;
    private String processType;
    @JsonProperty("requestorProcessId")
    private String requestId;
    private boolean approved;
    private String reason;
    private String timestamp;

}
