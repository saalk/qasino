package cloud.qasino.card.configuration;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = {"nl.knikit.card"})
@EnableTransactionManagement
public class HibernateConfiguration {

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(
                new String[]{"nl.knikit.card"});
        sessionFactory.setHibernateProperties(hibernateProperties());

        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        //dataSource.setDriverClassName("org.h2.Driver");
        //dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");

        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://192.168.2.100:3306/knikit");
        dataSource.setUsername("");
        dataSource.setPassword("");

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager
                = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }

    // uses sessionBuilder.addProperties(getHibernateProperties()) in the session factory bean
    // or seat each property with sessionBuilder.setProperty("hibernate.show_sql", "true");
    final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();

        hibernateProperties.put("hibernate.show_sql", "true");
        // @ing use org.hibernate.dialect.Oracle10gDialect
        // This property makes Hibernate generate the appropriate SQL for the chosen database.
        // do not use org.hibernate.dialect.MySQLInnoDBDialect or org.hibernate.dialect.MySQLDialect
        //hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MariaDB53");
        //hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");

        //properties.put("hibernate.connection.driver_class", "com.mariadb.jdbc.Driver");

        //switch to a in memory database
        //hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        hibernateProperties.put("hibernate.hbm2ddl.auto", "create");
        hibernateProperties.put("hibernate.hbm2ddl.import_files", "initial_data.sql");

        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDB53Dialect");
        hibernateProperties.setProperty("hibernate.connection.autocommit", "false");
        hibernateProperties.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
        hibernateProperties.setProperty("hibernate.id.new_generator_mappings", "true");
        hibernateProperties.setProperty("hibernate.enable_lazy_load_no_trans", "true");

        hibernateProperties.setProperty("hibernate.jdbc.batch_versioned_data", "true");
        hibernateProperties.setProperty("hibernate.order_inserts", "true");
        hibernateProperties.setProperty("hibernate.order_updates", "true");

        return hibernateProperties;
    }
}