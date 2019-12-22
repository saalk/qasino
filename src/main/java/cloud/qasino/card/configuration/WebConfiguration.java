package cloud.qasino.card.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"nl.knikit.card"})
public class WebConfiguration {

    @Bean(name = "modelmapper")
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // A: Configure a session bean for Player from the session factory etc
    // SessionFactory is to create and manage Sessions, one per app and singleton
    // Session is to provide a CRUD interface for mapped classes, one per client
/*    @Bean(name = "sessionFactory")
    public LocalSessionFactoryBean sessionFactory() throws UnsupportedEncodingException, SQLException {
        final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        // If you want to add more classes -> (User.class, Object.class);
        // sessionBuilder.addAnnotatedClasses(Player.class);
        // or just scan the model package
        sessionFactory.setDataSource(restDataSource());
        sessionFactory.setPackagesToScan("nl.knikit.card");
        sessionFactory.setHibernateProperties(hibernateProperties());

        return sessionFactory;
    }*/

    // B: create a DataSource is to be used with Hibernate’s SessionFactory
    // DataSource is a "Connection" to the database
    // Sessions work on the data via a datasource
 /*   @Bean(name = "restDataSource")
    public DriverManagerDataSource restDataSource() {
        //MariaDbDataSource driverManagerDataSource = new MariaDbDataSource();
        // org.mariadb.jdbc.MySQLDataSource or com.mariadb.jdbc.Driver
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        // JDBC drivers are extensions for java to connect to the database

        // either these 3 or URL
        driverManagerDataSource.setDriverClassName(Preconditions.checkNotNull("org.mariadb.jdbc.Driver"));
        driverManagerDataSource.setUsername(Preconditions.checkNotNull("fill"));
        driverManagerDataSource.setPassword(Preconditions.checkNotNull("fill"));
        driverManagerDataSource.setUrl(Preconditions.checkNotNull("jdbc:mariadb://192.168.2.100:3306/knikit"));

        return driverManagerDataSource;
*/
        /*BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("org.mysql.jdbc.Driver"));
        dataSource.setUrl(env.getProperty("jdbc:mysql://192.168.2.100:3306/knikit"));
        dataSource.setUsername(env.getProperty(""));
        dataSource.setPassword(env.getProperty(""));*/
        //dataSource.setUsername(env.getProperty(Password.decode("")));
        //dataSource.setPassword(env.getProperty(Password.decode("")));
        //return dataSource;
//    }

    // C: By configuring a transaction manager, code in the dao class doesn’t have to take care of
    // transaction management explicitly. Instead, the @Transactional annotation can be used.

 /*   @Bean(name = "transactionManager")
    @Autowired
    public HibernateTransactionManager transactionManager(final SessionFactory sessionFactory) {
        final HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);

        return txManager;
    }

    // D: Translate all errors generated during the persistence process (HibernateExceptions, PersistenceExceptions...)
    // into DataAccessException objects
    @Bean(name = "exceptionTranslation")
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

*/


    // uses sessionBuilder.addProperties(getHibernateProperties()) in the session factory bean
    // or seat each property with sessionBuilder.setProperty("hibernate.show_sql", "true");
  /*  final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.show_sql", "true");
        // @ing use org.hibernate.dialect.Oracle10gDialect
        // This property makes Hibernate generate the appropriate SQL for the chosen database.
        // do not use org.hibernate.dialect.MySQLInnoDBDialect or org.hibernate.dialect.MySQLDialect
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        //properties.put("hibernate.connection.driver_class", "com.mariadb.jdbc.Driver");

        //switch to a in memory database
        //hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        hibernateProperties.put("hibernate.hbm2ddl.auto", "create");
        hibernateProperties.put("hibernate.hbm2ddl.import_files", "initial_data.sql");

        hibernateProperties.setProperty("hibernate.connection.autocommit", "false");
        hibernateProperties.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
        hibernateProperties.setProperty("hibernate.id.new_generator_mappings", "true");
        hibernateProperties.setProperty("hibernate.enable_lazy_load_no_trans", "true");

        hibernateProperties.setProperty("hibernate.jdbc.batch_versioned_data", "true");
        hibernateProperties.setProperty("hibernate.order_inserts", "true");
        hibernateProperties.setProperty("hibernate.order_updates", "true");

        return hibernateProperties;
    }*/
}
