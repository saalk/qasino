package applyextra.operations.dto;

import lombok.Getter;
import applyextra.commons.model.CorrespondenceAddress;
import nl.ing.sc.individualmanagement.value.Gender;
import nl.ing.sc.individualmanagement.value.address.Address;
import nl.ing.sc.individualmanagement.value.address.Country;
import org.joda.time.DateTime;

@Getter
public final class IndividualDetailsDTO {

	private final String id;
	private final String correspondenceName;
	private final DateTime bornDate;
	private final Country citizenCountry;
	private final Gender gender;
	private final String phoneNumber;
	private final Address residentialAddress;
	private final CorrespondenceAddress correspondenceAddress;


	private IndividualDetailsDTO(final Builder builder) {
		id = builder.id;
		correspondenceName = builder.correspondenceName;
		bornDate = builder.bornDate;
		citizenCountry = builder.citizenCountry;
		gender = builder.gender;
		phoneNumber = builder.phoneNumber;
		residentialAddress = builder.residentialAddress;
		correspondenceAddress = builder.correspondenceAddress;
	}

	public static class Builder {

		private String id;
		private String correspondenceName;
		private DateTime bornDate;
		private Country citizenCountry;
		private Gender gender;
		private String phoneNumber;
		private Address residentialAddress;
		private CorrespondenceAddress correspondenceAddress;

		public Builder withId(final String id) {
			this.id = id;
			return this;
		}

		public Builder withCorrespondenceName(final String correspondenceName) {
			this.correspondenceName = correspondenceName;
			return this;
		}

		public Builder withBornDate(final DateTime bornDate) {
			this.bornDate = bornDate;
			return this;
		}

		public Builder withCitizenCountry(final Country citizenCountry) {
			this.citizenCountry = citizenCountry;
			return this;
		}

		public Builder withGender(final Gender gender) {
			this.gender = gender;
			return this;
		}

		public Builder withPhoneNumber(final String phoneNumber) {
			this.phoneNumber = phoneNumber;
			return this;
		}

		public Builder withResidentialAddress(final Address residentialAddress) {
			this.residentialAddress = residentialAddress;
			return this;
		}

		public Builder withCorrespondenceAddress(final CorrespondenceAddress correspondenceAddress) {
			this.correspondenceAddress = correspondenceAddress;
			return this;
		}

		public IndividualDetailsDTO build() {
			return new IndividualDetailsDTO(this);
		}

	}

}
