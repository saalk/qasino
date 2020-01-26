package applyextra.commons.configuration;

import lombok.extern.slf4j.Slf4j;
import applyextra.api.parties.individuals.configuration.IndividualsAllResourceClientConfiguration;
import nl.ing.riaf.core.configuration.BasicConfiguration;
import nl.ing.riaf.core.util.JNDIUtil;
import nl.ing.riaf.ix.configuration.IXCoreConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;

@Configuration
@Import({BasicConfiguration.class,
        IXCoreConfiguration.class,
        IndividualsAllResourceClientConfiguration.class,
        InternalScoringConfiguration.class
})
@EnableJpaRepositories("applyextra.commons.dao.request")
@EnableTransactionManagement
@Lazy
@Slf4j
public class FinancialAcceptanceConfiguration {

    @Resource
    private JNDIUtil jndiUtil;

    @Bean(name = "decisionScoreExpirationTime")
    public Integer getDecisionScoreExpirationTime() {
        return jndiUtil.getJndiIntValue("param/creditscore/expiration/inhours", true);
    }

    @Bean(name = "applicationId")
    public String getApplicationId() {
        return jndiUtil.getJndiValue("param/creditscore/application-id", true);
    }

    @Bean(name = "initiatedChannel")
    public String getInitiatedChannel() {
        return jndiUtil.getJndiValue("param/creditscore/initiated-channel", true);
    }

    @Bean(name = "participantNumber")
    public Integer getParticipantNumber() {
        return jndiUtil.getJndiIntValue("param/creditscore/participant-number", true);
    }

    @Bean(name = "systemName")
    public String getSystemName() {
        return jndiUtil.getJndiValue("param/creditscore/system-name", true);
    }

    @Bean(name = "userId")
    public String userId() {
        return jndiUtil.getJndiValue("param/creditscore/user-id", true);
    }
}
