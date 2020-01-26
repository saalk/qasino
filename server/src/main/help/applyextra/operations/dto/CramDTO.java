package applyextra.operations.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CramDTO {

	private String requestType;
	private String requestId;
	private String origin;
	private String partyId;
	private String mdmCode;
	private String mdmName;
	private String notificationQueue;
	private RiskLevel riskLevel;
	private String smsText;

	private CramStatus status;
	private String cramId;
	private Boolean confirmResult;

	private String errorCode;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Date expirationDate;
	private String description; // default value

	// set auth text and subject for digital 20 in app auth
	private String authSubject;
	private String authText;

	/*
	 * STSK0119922 - Override Lombok getter and setter for expiration Date to perform a deep-copy of the variable to
	 * comply with Sonar.
	 */
	public Date getExpirationDate() {
		return new Date(this.expirationDate.getTime());
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = new Date(expirationDate.getTime());
	}

	// Risk level affects the way the customer has to authorize his request.
	// The gAuthorizerAPI will select different customer authorization means based on this.
	// Assisted channels will require different level of authentication based on this.
	public enum RiskLevel {
		LOW(1),
		MILD(2),
		MEDIUM(3);

		@Getter
		private Integer serviceValue;

		RiskLevel(Integer serviceXmlValue) {
			this.serviceValue = serviceXmlValue;
		}
	}

}
