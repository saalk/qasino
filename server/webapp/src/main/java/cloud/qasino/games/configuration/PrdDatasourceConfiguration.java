package cloud.qasino.games.configuration;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

//@Component
//@Profile("prd")
public class PrdDatasourceConfiguration implements DatasourceConfig {
//    @Override
    public void setup() {
        System.out.println("Setting up datasource for PRODUCTION environment. ");
    }
}
