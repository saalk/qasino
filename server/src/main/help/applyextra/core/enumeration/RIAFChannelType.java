package applyextra.core.enumeration;

/**
 * Identify the channel type of an application
 */
public enum RIAFChannelType {

    /** branches (kantoor) */
    BRANCHES("001"),
    /** MidOfficces */
    MIDOFFICES("002"),
    /** Internet channel type */
    INTERNET("003"),
    /** call */
    CALL("004"),
    /** OperationServices (OS) */
    OS("005"),
    /** RapidSugar */
    RAPIDSUGAR("006"),
    /** MDM */
    MDM("007"),
    /** RGB */
    RGB("008"),
    /** OEM */
    OEM("009");

    private final String code;

    /**
     * Constructor
     * 
     * @param code
     *            The code that belongs to a given channel.
     */
    RIAFChannelType(final String code) {
	this.code = code;
    }

    /**
     * Retrieve the code
     * 
     * @return the code
     */
    public String getCode() {
	return code;
    }
}
