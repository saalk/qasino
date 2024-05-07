package cloud.qasino.games.config;

//@Component
//@Profile("dev")
public class DevDatasourceConfiguration implements DatasourceConfig {
//    @Override
    public void setup() {
        System.out.println("Setting up datasource for DEV environment. ");
    }
}
