package cloud.qasino.games.config;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Profile("h2")
@PropertySource("classpath:application-h2-database.properties")
public class DatasourceH2Configuration implements DatasourceConfig {
    @Override
    public void setup() {
        System.out.println("Setting up datasource for DEV environment. ");
    }
}
