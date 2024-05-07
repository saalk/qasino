package cloud.qasino.games.config;

//@Component
//@Profile("prd")
public class PrdDatasourceConfiguration implements DatasourceConfig {
//    @Override
    public void setup() {
        System.out.println("Setting up datasource for PRODUCTION environment. ");
    }
}
