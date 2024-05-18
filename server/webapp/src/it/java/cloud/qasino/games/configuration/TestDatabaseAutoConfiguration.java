//package cloud.qasino.games.httpcalls;
//
//import cloud.qasino.games.database.configuration.DatabaseAutoConfiguration;
//import cloud.qasino.games.database.configuration.DatabaseProperties;
//import lombok.extern.slf4j.Slf4j;
//import org.hibernate.event.service.spi.EventListenerRegistry;
//import org.hibernate.event.spi.EventType;
//import org.hibernate.internal.SessionFactoryImpl;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//
//import javax.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//
///**
// * Test configuration class to load default h2 properties for integration tests
// */
//@Slf4j
//@TestConfiguration
//public class TestDatabaseAutoConfiguration extends DatabaseAutoConfiguration {
//
//    public TestDatabaseAutoConfiguration(DatabaseProperties databaseProperties) {
//        super(databaseProperties);
////        super(databaseProperties, meterRegistry);
//
//    }
//
//    @Bean
//    @Override
//    public DataSource dataSource() {
//        log.warn("Datasource for in-memory database");
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
//        dataSource.setVisitorname("sa");
//        dataSource.setPassword("");
//        return dataSource;
//    }
//
//    @Bean
//    @Override
//    public EntityManagerFactory entityManagerFactory(final DataSource dataSource,
//                                                     @Value("${param.database.generate.tables:true}") final boolean isGeneratingTables,
//                                                     @Value("${param.database.show.sql:true}") final boolean isShowingSql) {
//        log.info("Entity Manager Factory for in-memory database");
//        HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
//        vendor.setGenerateDdl(isGeneratingTables);
//        vendor.setShowSql(isShowingSql);
//
//        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//        factory.setDataSource(dataSource);
//        factory.setJpaVendorAdapter(vendor);
//        factory.setPackagesToScan(getPackagesToScan().toArray(new String[0]));
//        factory.afterPropertiesSet();
//
//        //Setting the pre insert and pre update event listeners
//        EntityManagerFactory emf = factory.getObject();
//        SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
//        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
//
//        TestDatabaseTriggerHandler testDatabaseTriggerHandler = new TestDatabaseTriggerHandler();
//        registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(testDatabaseTriggerHandler);
//        registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(testDatabaseTriggerHandler);
//        return emf;
//    }
//
//}
