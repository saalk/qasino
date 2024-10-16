package cloud.qasino.games.config;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Profile("postgresql")
@PropertySource("classpath:application-postgresql-database.properties")
public class DatasourcePostgresqlConfiguration implements DatasourceConfig {
//    @Override
    public void setup() {
        System.out.println("Setting up datasource for PRODUCTION environment. ");
    }
}
