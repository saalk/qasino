package applyextra.commons.configuration;

import javax.validation.constraints.NotNull;

/**
 * Bean to dynamically add entity packages to the entity manager.
 *
 * These beans MUST be created eagerly created in the configuration classes. It can be created multiple times for different
 * packages. If the same package is created twice, then one of the is added randomly into the entity manager.
 */
public final class JPAModelPackage {

    private final Package modelPackage;

    /**
     * Single constructor with the package for a model
     *
     * @param modelPackage the package of the model
     */
    public JPAModelPackage(@NotNull final Package modelPackage) {
        this.modelPackage = modelPackage;
    }

    /**
     * Get the actual package class for this model
     *
     * @return non-null package class
     */
    public final Package getModelPackage() {
        return modelPackage;
    }

    @Override
    public final boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof JPAModelPackage) {
            return modelPackage.equals(((JPAModelPackage) o).modelPackage);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return modelPackage.hashCode();
    }

}
