package applyextra.commons.filter;

import nl.ing.serviceclient.lecca.dto.CreditCard;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * AbstractFilter for setting up filtering on multiple values on a CreditCards element
 *
 * @author Klaas.van.der.Meulen
 */

public abstract class AbstractFilter {

    public abstract boolean matches(CreditCard creditCard);

    public abstract void setFilterValue(List<String> inputFilterValues);

    public final
    @NotNull
    AbstractFilter makeNew() {
        try {
            return getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
