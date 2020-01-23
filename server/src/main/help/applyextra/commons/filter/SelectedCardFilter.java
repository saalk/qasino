package applyextra.commons.filter;

import nl.ing.serviceclient.lecca.dto.CreditCard;

import java.util.List;

public class SelectedCardFilter extends AbstractFilter {
	public SelectedCardFilter(String filterValue) {
		this.filterValue = filterValue;
	}

	private String filterValue;

	public boolean matches(CreditCard creditCard) {
		return creditCard.getCreditCardNumber().endsWith(filterValue);
	}

	@Override
	@Deprecated // for support only, best to use constructor
	public void setFilterValue(List<String> inputFilterValues) {
		filterValue = inputFilterValues.get(0);
	}

}
