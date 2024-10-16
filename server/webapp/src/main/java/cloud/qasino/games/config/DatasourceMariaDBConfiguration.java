package cloud.qasino.games.config;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Profile("mariadb")
@PropertySource("classpath:application-mariadb-database.properties")
public class DatasourceMariaDBConfiguration implements DatasourceConfig {
    @Override
    public void setup() {
        System.out.println("Setting up datasource for DEV environment. ");
    }
}
