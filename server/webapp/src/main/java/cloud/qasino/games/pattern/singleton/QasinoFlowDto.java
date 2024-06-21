package cloud.qasino.games.pattern.singleton;

public class QasinoFlowDto {

    private static QasinoFlowDto instance = null;

    private String customAttribute;

    public QasinoFlowDto() {
        //default constructor stuff here
    }

    // important singleton function
    public static QasinoFlowDto getInstance() {
        if (instance == null)
            instance = new QasinoFlowDto();
        return instance;
    }
}
