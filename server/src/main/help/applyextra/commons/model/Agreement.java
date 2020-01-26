package applyextra.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agreement {

	private CorrespondenceAddress correspondenceAddress;
	private String role;
    private String accountHolderName;

	@JsonIgnore
	private String cardHolderId;

	@JsonIgnore
	private String accountHolderId;

	@JsonIgnore
	private String arrangementId;
}
