package applyextra;
import java.util.Set;

public interface Customers extends Validatable {
    Customer getActiveCustomer();

    Set<Customer> getAllCustomers();
}