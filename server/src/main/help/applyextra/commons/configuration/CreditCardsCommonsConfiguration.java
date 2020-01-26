package applyextra.commons.configuration;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.components.retry.CreditCardRequestRetryManager;
import applyextra.commons.components.retry.CreditCardRetryConfiguration;
import applyextra.commons.model.AccountCategory;
import applyextra.commons.util.MiscUtils;
import nl.ing.riaf.core.configuration.BasicConfiguration;
import nl.ing.riaf.core.util.PasswordUtil;
import nl.ing.riaf.ix.configuration.IXCoreConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@Configuration
@Import({BasicConfiguration.class, IXCoreConfiguration.class })
@EnableJpaRepositories({"applyextra.commons.dao.request"})
@EnableTransactionManagement
@ComponentScan(value = {"applyextra.commons",
        "applyextra.operations",
        "applyextra.businessrules",
        "applyextra.api.service"},
        excludeFilters = {
            // Retry manager cannot be lazy initialised, so it is excluded
            // however the CreditCardRetryConfiguration is lazy
            @ComponentScan.Filter(value = {
                    CreditCardRequestRetryManager.class, CreditCardRetryConfiguration.class,
                    CreditCardsCommonServicesConfiguration.class
            },
                    type = FilterType.ASSIGNABLE_TYPE)
        },
        lazyInit = true)
@Lazy
@Slf4j
public class CreditCardsCommonsConfiguration {

    @Resource
    private PasswordUtil passwordUtil;

    @Resource
    private ApplicationContext context;


    @Bean
    public JPAModelPackage commonsRequestPackageName() {
        return new JPAModelPackage(AccountCategory.class.getPackage());
    }

    @Bean(name = "dataSource")
    public DriverManagerDataSource dataSource() throws IOException {
        Properties prop = MiscUtils.loadProperties("database", DriverManagerDataSource.class);
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName(prop.getProperty("jdbc.driverClassName"));
        driverManagerDataSource.setUrl(prop.getProperty("jdbc.url"));
        driverManagerDataSource.setUsername(prop.getProperty("jdbc.username"));
        final String passwordProperty = prop.getProperty("jdbc.password");
        driverManagerDataSource.setPassword(passwordUtil.decode(passwordProperty));
        return driverManagerDataSource;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() throws IOException {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);

        // Gather the created JPAModelPackage beans
        Collection<JPAModelPackage> modelPackages = context.getBeansOfType(JPAModelPackage.class).values();
        // Convert the model beans into a set of strings containing the package names
        Set<String> modelPackageNames = new HashSet<>();
        for (JPAModelPackage modelPackage : modelPackages) {
            log.info("[JPA-PACKAGES] Adding " + modelPackage.getModelPackage().getName());
            modelPackageNames.add(modelPackage.getModelPackage().getName());
        }
        //modelPackageNames.add("");

        // Add the package name set to the entity manager factory
        factory.setPackagesToScan(modelPackageNames.toArray(new String[modelPackageNames.size()]));
        factory.setDataSource(dataSource());
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) throws IOException {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean(name = "messageSource")
    public ResourceBundleMessageSource initResourceBundle() {
        ResourceBundleMessageSource resource = new ResourceBundleMessageSource();
        resource.setBasename("CommonValidationMessages");
        return resource;
    }
}
