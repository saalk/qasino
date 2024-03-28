package cloud.qasino.card.util;

import java.util.function.Function;

// example:
// if (new NullCheck<>(person).with(Person::getAddress).with(Address::getZipCode).isNull())

public class NullCheck<T> {
    private T root;

    public NullCheck(T root) {
        this.root = root;
    }

    // build a small object graph that uses Functions to walk the graph being checked
    public <C> NullCheck<C> with(Function<T, C> getter) {
        return root != null ? new NullCheck<>(getter.apply(root)) : new NullCheck<>(null);
    }

    public boolean isNull() {
        return root == null;
    }

    public boolean isNotNull() {
        return root != null;
    }
}