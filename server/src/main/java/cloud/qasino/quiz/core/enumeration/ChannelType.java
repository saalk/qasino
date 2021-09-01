package cloud.qasino.quiz.core.enumeration;

public enum ChannelType {

    BRANCHES("001"),
    MIDOFFICES("002"),
    INTERNET("003"),
    CALL("004"),
    OPERATION_SERVICES("005"),
    MOBILE("006");

    private final String code;

    ChannelType(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
