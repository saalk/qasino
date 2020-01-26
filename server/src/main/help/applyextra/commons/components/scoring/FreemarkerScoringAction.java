package applyextra.commons.components.scoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import applyextra.api.service.FreemarkerAttributeExtractor;
import applyextra.commons.configuration.InternalScoringConfiguration;
import applyextra.pega.api.common.util.ConnectionSettings;
import applyextra.pega.api.common.util.KeyStoreSettings;
import applyextra.pega.api.model.scoring.ScoringRequest;
import applyextra.pega.api.model.scoring.ScoringResponse;
import nl.ing.riaf.core.config.spring.RiafService;
import nl.ing.riaf.ix.freemarker.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
@RiafService(name = InternalScoringConfiguration.SERVICE_NAME, profile = "freemarker")
public class FreemarkerScoringAction implements BaseScoring {
    /**
     * Regex used to retrieve partyId from JSON
     */
    private static final String EXTRACTED_ATTRIBUTES = "\"customerIdentifier\":\"([0-9]+)\"";
    /**
     * The name the freemarker will recognize as this value
     */
    private static final String PARTY_ID = "partyId";

    private static final String TARGET_NAME = InternalScoringConfiguration.SERVICE_NAME.toLowerCase();

    private Configuration freemarkerConf;
    private Template template;

    FreemarkerAttributeExtractor attributeExtractor = new FreemarkerAttributeExtractor();

    @Override
    public void init(ConnectionSettings connectionSettings, KeyStoreSettings trustedPublicKeySettings, String path) {
        freemarkerConf = attributeExtractor.getTemplateConfig();
        try {
            template = freemarkerConf.getTemplate(InternalScoringConfiguration.SERVICE_NAME.toLowerCase() + ".ftl");
        } catch (IOException e) {
            log.error("Missing ftl file :" + InternalScoringConfiguration.SERVICE_NAME.toLowerCase() + ".ftl");
        }
    }

    @Override
    public ScoringResponse perform(ScoringRequest request) throws NoSuchAlgorithmException, IOException, KeyManagementException {
        return result(request);
    }

    private ScoringResponse result(ScoringRequest scoringRequest) {
        Writer rawJsonResponse = new StringWriter();
        ScoringResponse response;

        final ObjectMapper mapper = new ObjectMapper();
        try {
            SimpleServiceCharacteristics ssc = attributeExtractor.getCharacteristics(PARTY_ID,TARGET_NAME,EXTRACTED_ATTRIBUTES);
            PatternDrivenExtractionModel pdem = attributeExtractor.getTemplateModel(scoringRequest.toString(),ssc);

            template.process(pdem, rawJsonResponse);
            response = mapper.readValue(rawJsonResponse.toString(), ScoringResponse.class);
        } catch (IOException | TemplateException e) {
            throw new IllegalStateException(
                    "type " + ScoringResponse.class.getName() + " can not be instantiated from JSON", e);
        }
        return response;
    }
}
