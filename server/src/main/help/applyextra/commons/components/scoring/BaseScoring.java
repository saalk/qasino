package applyextra.commons.components.scoring;

import applyextra.pega.api.common.util.ConnectionSettings;
import applyextra.pega.api.common.util.KeyStoreSettings;
import applyextra.pega.api.model.scoring.ScoringRequest;
import applyextra.pega.api.model.scoring.ScoringResponse;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public interface BaseScoring {
    void init(ConnectionSettings connectionSettings, KeyStoreSettings
            trustedPublicKeySettings, String path);

    ScoringResponse perform(ScoringRequest request) throws NoSuchAlgorithmException, IOException, KeyManagementException;
}
