package applyextra.core;

import applyextra.core.enumeration.RIAFChannelType;

import java.util.Set;

/**
 * Interface for a RIAF Application.
 * <p>
 * The purpose of this interface is to contain abstract information about a RIAF application
 * The interface is used in the AbstractIntegrationConfiguration and must therefore
 * be implemented in each configuration object of all applications.
 * <p>
 * If the interface is not implemented the application will not start.
 */
public interface RIAFBaseApplication {
    /**
     * Get the application part of the problem report code for this application
     *
     * @return the integer that defines the application part of a Problem Reporting Code
     */
    String getApplicationId();

    Set<String> getAllowedUserTypes();

    /**
     * Get the channel type for this application (e.g. Internet)
     *
     * @return the channel type
     */
    RIAFChannelType getChannelType();

    /**
     * Defines whether the application requires the agreementId and
     * agreementType in the Identity object.
     * <p>
     * This flag is introduced to ensure "old" RIAF application behavior
     * for applications that use the  Identity.getAgreementId()
     * and Identity.getAgreementType() methods. This old behavior
     * ensures that when no agreementid or agreementtype is passed,
     * the Identity object can not be created and a IdentityException
     * is thrown; the user will not be able to enter the application.
     *
     * @return false for new applications or applications that do not
     * require agreement information to be available in the Identity
     * object. New applications that require the agreement
     * information should handle the absence of the agreement information
     * appropriately. return true for applications that require old
     * RIAF behavior.
     */
    boolean isIdentityAgreementRequired();
}
