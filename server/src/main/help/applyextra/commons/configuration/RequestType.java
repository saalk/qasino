package applyextra.commons.configuration;

public enum RequestType implements ContextType {

    RESEND_PIN,
    REPLACE_CARD,
    CHANGE_REPAYMENT_OFF,
    CHANGE_REPAYMENT_ON("Gespreid betalen aanvragen"),
    ACTIVATE_CARD,
    CHANGE_LIMIT("Limiet wijzigen"),
    CHANGE_LIMIT_DECREASE("Limiet verlagen"),
    CHANGE_LIMIT_INCREASE("Limiet verhogen"),
    APPLYCARD("Kaart aanvragen"),
    APPLY_EXTRA_CARD,
    CLOSE_CARD,
    REGISTRATIONS;

    /**
     * Nice names for pega. If these are not supplied, then they will be set to the id of the enum. They are also used for
     * matching if the id could not be found.
     */
    private final String pegaProcessName;

    RequestType(){
        this(null);
    }

    //TODO ids for SIA etc. might go in private int processId
    RequestType(final String pegaProcessName){
        if (pegaProcessName == null) {
            this.pegaProcessName = getId();
        } else {
            this.pegaProcessName = pegaProcessName;
        }
    }

    @Override
    public String getId() {
        return toString();
    }

    /**
     * First search for a match using the id. If no match was found, then search the pega names for a match.
     *
     * @param type either the type id, or the pega name
     * @return the matching request type or null
     */
    public static RequestType getRequestType(final String type) {
        for(RequestType requestType : values()) {
            if (requestType.getId().equals(type)) {
                return requestType;
            }
        }
        for(RequestType requestType : values()) {
            if (requestType.getPegaProcessName().equals(type)) {
                return requestType;
            }
        }
        return null;
    }

    public String getPegaProcessName() {
        return pegaProcessName;
    }

}
