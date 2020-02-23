package applyextra.commons.filter;

import nl.ing.serviceclient.lecca.dto.CreditCard;

import java.util.List;

public class StatusFilter extends AbstractFilter {
	private String filterValue;

    public boolean matches(CreditCard creditCard) {
        return filterValue.contains(creditCard.getActivationStatus().toLowerCase());
    }

    @Override
    public void setFilterValue(List<String> inputFilterValues) {
        StringBuilder filterValueBuilder = new StringBuilder();
        for (String entry : inputFilterValues) {
            filterValueBuilder
					.append(StatusTypes.fromString(entry).filterMatch.toLowerCase())
					.append(",");
        }
        filterValue = filterValueBuilder.toString();
    }

	enum StatusTypes{
		INACTIVE("inactive", "No"),
		ACTIVE("active", "Yes");
		private final String filterName;

		private final String filterMatch;


		StatusTypes(String filterName, String filterMatch){
			this.filterName = filterName;
			this.filterMatch = filterMatch;
		}

		public static StatusTypes fromString(String stringValue) throws IllegalArgumentException{
			if (stringValue != null) {
				for (StatusTypes enumValues : StatusTypes.values()) {
					if (stringValue.equalsIgnoreCase(enumValues.filterName)) {
						return enumValues;
					}
				}
			}
			throw new IllegalArgumentException("No StatusFilter.StatusTypes enum found for value : " + stringValue);
		}

	}
}
