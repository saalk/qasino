package cloud.qasino.quiz.core.context;
import cloud.qasino.quiz.core.context.Customer;
import org.hibernate.validator.internal.metadata.facets.Validatable;

import java.util.Set;

public interface Customers extends Validatable {
    cloud.qasino.quiz.core.context.Customer getActiveCustomer();

    Set<Customer> getAllCustomers();
}