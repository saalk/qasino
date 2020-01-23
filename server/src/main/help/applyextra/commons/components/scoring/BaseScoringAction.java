package applyextra.commons.components.scoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import applyextra.commons.configuration.InternalScoringConfiguration;
import applyextra.pega.api.common.BasicHttpsApiCaller;
import applyextra.pega.api.common.util.ConnectionSettings;
import applyextra.pega.api.common.util.KeyStoreSettings;
import applyextra.pega.api.model.scoring.ScoringRequest;
import applyextra.pega.api.model.scoring.ScoringResponse;
import nl.ing.riaf.core.config.spring.RiafService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
@RiafService(name = InternalScoringConfiguration.SERVICE_NAME, profile = "live")
public class BaseScoringAction implements BaseScoring{

    private BasicHttpsApiCaller scoringApiCaller;
    private String path;

    @Override
    public void init(ConnectionSettings connectionSettings, KeyStoreSettings
            trustedPublicKeySettings, String path) {
        scoringApiCaller = new BasicHttpsApiCaller(connectionSettings, trustedPublicKeySettings);
        this.path = path;
    }

    @Override
    public ScoringResponse perform(ScoringRequest request) throws NoSuchAlgorithmException, IOException, KeyManagementException {
        log.debug("Sending a scoring request: "+ request.toString());
        String response = scoringApiCaller.call(path, "POST", request.toString());
        log.debug("got response from scoring API: " + response);
        return restoreAdditionalContextDataFromJson(response);
    }

    private static ScoringResponse restoreAdditionalContextDataFromJson(String jsonString) {

        final ScoringResponse result;
        final ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.readValue(jsonString, ScoringResponse.class);
        } catch (final IOException e) {
            throw new IllegalStateException(
                    "type " + ScoringResponse.class.getName() + " can not be instantiated from JSON", e);
        }
        return result;
    }
}
