package cloud.qasino.games.core.context;
import org.hibernate.validator.internal.metadata.facets.Validatable;

import java.util.Set;

public interface Customers extends Validatable {
    Customer getActiveCustomer();

    Set<Customer> getAllCustomers();
}