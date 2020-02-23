package applyextra.commons.configuration;

import lombok.extern.slf4j.Slf4j;
import applyextra.pega.api.common.util.ConnectionSettings;
import applyextra.pega.api.common.util.KeyStoreSettings;
import nl.ing.riaf.core.configuration.BasicConfiguration;
import nl.ing.riaf.core.util.JNDIUtil;
import nl.ing.riaf.core.util.PasswordUtil;
import nl.ing.riaf.ix.configuration.IXCoreConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;

@Configuration
@Import({BasicConfiguration.class,
        IXCoreConfiguration.class
})
@Lazy
@Slf4j
public class InternalScoringConfiguration {

    public static final String SERVICE_NAME = "InternalScoringAPI";
    @Resource private JNDIUtil jndiUtil;
    @Resource private PasswordUtil passwordUtil;

    @Bean(name = "internalScoringConnection")
    public ConnectionSettings getInternalScoringConnection() {
        String baseUrl = jndiUtil.getJndiValue("param/services/scoring/base-url", true);
        long timeOut = jndiUtil.getJndiLongValue("param/services/scoring/timeout", true);
        String userName = jndiUtil.getJndiValue("param/services/scoring/username");
        char[] password = passwordUtil.decode(jndiUtil.getJndiValue("param/services/scoring/password")).toCharArray();

        return new ConnectionSettings(baseUrl, "application/json", userName, password);
    }

    @Bean(name = "internalScoringPublicKeySettings")
    public KeyStoreSettings getInternalScoringKeySettings() {
        String keyStorePath = jndiUtil.getJndiValueWithDefault("param/services/scoring/keyStoreLocation", null);
        char[] keyStorePassword = passwordUtil.decode(jndiUtil.getJndiValue("param/services/scoring/keyStorePassword"))
                .toCharArray();
        String keyAlias = jndiUtil.getJndiValue("param/services/scoring/keyAlias");

        KeyStoreSettings settings = null;

        if (keyStorePath != null) {
            settings = new KeyStoreSettings(keyStorePath, keyStorePassword, keyAlias);
        }

        return settings;
    }

    @Bean(name = "internalScoringUrlPath")
    public String getInternalScoringUrlPath() {
        return jndiUtil.getJndiValue("param/services/scoring/internal-scoring/path");
    }
}