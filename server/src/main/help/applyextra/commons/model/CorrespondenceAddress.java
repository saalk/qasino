package applyextra.commons.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by CU25AF on 20-5-2015.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CorrespondenceAddress {
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String accountHolderName;
}
