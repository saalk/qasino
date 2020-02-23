package applyextra.configuration;

import applyextra.api.application.configuration.RRBMQConfiguration;
import applyextra.CreditcardsApplyExtraCardApplication;
import applyextra.api.listaccounts.creditcards.configuration.*;
import applyextra.api.parties.individuals.configuration.IndividualsAllResourceClientConfiguration;
import applyextra.api.sendcustomermessage.global.configuration.SendCustomerMessageAPIWrapperConfiguration;
import applyextra.commons.components.retry.CreditCardRetryConfiguration;
import applyextra.commons.configuration.CreditCardAsyncConfiguration;
import applyextra.commons.configuration.CreditCardsCommonServicesConfiguration;
import applyextra.commons.configuration.CreditCardsCommonsConfiguration;
import applyextra.commons.helper.GraphiteHelper;
import applyextra.commons.helper.GraphiteHelperImpl;
import nl.ing.riaf.core.RIAFBaseApplication;
import nl.ing.riaf.core.configuration.BasicConfiguration;
import nl.ing.riaf.core.util.JNDIUtil;
import nl.ing.riaf.core.util.PropertyFileJNDIUtilImpl;
import nl.ing.riaf.presentation.configuration.PeerToPeerTrustConfiguration;
import nl.ing.riaf.presentation.configuration.RestConfiguration;
import nl.ing.riaf.presentation.prometheus.configuration.PrometheusMetricsConfiguration;
import nl.ing.sc.arrangementretrieval.listarrangements2.configuration.ListArrangementsConfigurationNoContext;
import nl.ing.sc.arrangementretrieval.listparties2.configuration.ListPartiesWrapperConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;

import static applyextra.configuration.Constants.CONSUMER_APPLICATION_NAME;

/**
 *  Spring Configuration of the applyextracard application.
 */
@Configuration
@Import({
        CreditCardsCommonsConfiguration.class,
        CreditCardsCommonServicesConfiguration.class,
        CreditCardAsyncConfiguration.class,
        CreditCardRetryConfiguration.class
})
@ComponentScan({ "applyextra.api.application", "applyextra.api.applyextracard"})

public class ApplyExtraCardConfiguration {
    private PropertyFileJNDIUtilImpl jndiUtil;
    private String[] PROPERTY_FILES = new String[]{"jndi.properties", "caching.properties", "constants.properties","tibco.properties","web.properties","services.properties","mq.properties"};

    @Bean
    public GraphiteHelper graphiteHelper() {
        return new GraphiteHelperImpl();
    }

    @Bean
    public String consumerName() {
        return CONSUMER_APPLICATION_NAME;
    }

    /**
     * Returns the JndiUtil
     * These properties files contains different constant values.
     * jndi.properties : Contains generic property values
     * caching.properties : Contains rmi-caching related constants
     * constants.properties : Contains thresholds
     * web.properties : Contains property values for ming and other web related properties
     * @return JNDIUtil The jndi util
     */
    @Bean
    public JNDIUtil jndiUtil() {
        if (jndiUtil==null){
            jndiUtil = new PropertyFileJNDIUtilImpl(PROPERTY_FILES);
        }
        return jndiUtil;
    }

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

}