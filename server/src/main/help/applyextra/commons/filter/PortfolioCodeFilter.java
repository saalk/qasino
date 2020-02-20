package applyextra.commons.filter;

import nl.ing.serviceclient.lecca.dto.CreditCard;

import java.util.List;

/**
 * Implementation of filter for CreditCard for element portfolioCode
 *
 * @author Klaas.van.der.Meulen
 */
public class PortfolioCodeFilter extends AbstractFilter {

    private String filterValue;

    public boolean matches(CreditCard creditCard) {
        return filterValue.contains(creditCard.getPortfolioCode().toLowerCase());
    }

    @Override
    public void setFilterValue(List<String> inputFilterValues) {
        filterValue = "";
        for (String entry : inputFilterValues) {
            if ("charge".equalsIgnoreCase(entry)) {
                this.filterValue += "charge,";
            } else if ("revolving".equalsIgnoreCase(entry)) {
                this.filterValue += "revolving,";
            }
        }
    }
}
