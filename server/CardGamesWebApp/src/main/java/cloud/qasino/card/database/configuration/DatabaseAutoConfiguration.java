package cloud.qasino.card.database.configuration;

import cloud.qasino.card.database.entity.Game;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@AutoConfiguration
@ComponentScan("cloud.qasino.card.database") // loads all components in the module for requests.
@EnableJpaRepositories({"cloud.qasino.card.database.repository"})
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class DatabaseAutoConfiguration {

    private final DatabaseProperties databaseProperties;
//    private final MeterRegistry prometheusMeterRegistry;

    /**
     * Within credit cards, we prefer to programmatically create and manage our datasource beans, this overrides
     * any autoconfiguration created from spring.datasource.* properties. We do this for  several scenarios,
     * Main one being hte ability to spin up more than one datasource if. It also allows us to have more fine-grained control over the configuration.
     * It also allows one to be able to configure datasource based on runtime configuration. Lastly from a security standpoint, being able to
     * programmatically control the configuration of datasource, makes this setup the best choice.
     * @return {@link HikariDataSource} datasource instance
     */
    @Bean
    public DataSource dataSource() {
        HikariConfig hc = new HikariConfig();

        hc.setJdbcUrl(databaseProperties.getJdbcUrl());
        hc.setUsername(databaseProperties.getUsername());
        hc.setPassword(databaseProperties.getPassword());
        hc.setMaximumPoolSize(databaseProperties.getMaximumPoolSize());
        hc.setMinimumIdle(databaseProperties.getMinimumIdle());
        hc.setConnectionTimeout(databaseProperties.getConnectionTimeout());
        hc.setPoolName(databaseProperties.getPoolName());
        hc.setIdleTimeout(databaseProperties.getIdleTimeout());

        // datasource properties
        Properties dsProps = new Properties();
//        dsProps.put(oracle.jdbc.OracleConnection.CONNECTION_PROPERTY_THIN_NET_ENCRYPTION_LEVEL, "REQUIRED");
//        dsProps.put(oracle.jdbc.OracleConnection.CONNECTION_PROPERTY_THIN_NET_ENCRYPTION_TYPES, "( AES256 )");
        dsProps.put("oracle.jdbc.implicitStatementCacheSize", databaseProperties.getPrepStmtCacheSize());

        hc.setDataSourceProperties(dsProps);
//        hc.setMetricRegistry(prometheusMeterRegistry);

        return new HikariDataSource(hc);
    }

    /**
     * In a springboot application, if you declare your own datasource and entity manager and forget to configure the transaction manager, springboot will not automatically
     * configure a transaction manager for you. Spring autoconfiguration logic makes assumptions that you are taking control of the entire configuration and does not impose any additional
     * defaults. This is why we programmatically declare the bean as well.
     * @param entityManagerFactory entity
     * @return PlatformTransactionManager interface of the jpatransactionManager.
     */
    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }


    @Bean
    public EntityManagerFactory entityManagerFactory(final DataSource dataSource,
                                                     @Value("${param.database.generate.tables:false}") final boolean isGeneratingTables,
                                                     @Value("${param.database.show.sql:false}") final boolean isShowingSql) {

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdaptor(isShowingSql,isGeneratingTables));
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setPackagesToScan(getPackagesToScan().toArray(new String[0]));
        entityManagerFactoryBean.afterPropertiesSet();

        return entityManagerFactoryBean.getObject();
    }

    private HibernateJpaVendorAdapter vendorAdaptor(final boolean isShowingSql, final boolean isGeneratingTables) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(isShowingSql);
        vendorAdapter.setGenerateDdl(isGeneratingTables);
        return vendorAdapter;
    }

    public List<String> getPackagesToScan() {
        return Collections.singletonList(Game.class.getPackage().getName());
    }
}
