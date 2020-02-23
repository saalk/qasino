package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.api.parties.individuals.value.IndividualsAllBusinessResponse;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.components.scoring.BaseScoring;
import applyextra.commons.configuration.InternalScoringConfiguration;
import applyextra.commons.model.CreditScoreResult;
import applyextra.commons.model.database.entity.DecisionScoreEntity;
import applyextra.commons.model.database.entity.PartyEntity;
import applyextra.commons.orchestration.Action;
import applyextra.commons.orchestration.DecisionScoreType;
import applyextra.pega.api.common.util.ConnectionSettings;
import applyextra.pega.api.common.util.KeyStoreSettings;
import applyextra.pega.api.model.scoring.ScoringDecision;
import applyextra.pega.api.model.scoring.ScoringRequest;
import applyextra.pega.api.model.scoring.ScoringResponse;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;


/**
 * Note: import public key into keystore like so:
 * keytool -importcert -file pega-tst.europe.intranet-public.cer -keystore trusted-public-keys.pks -alias "Scoring"
 * You can use same password as used for api-trust.jks
 * <p>
 * check keystore like so:
 * keytool -list -keystore trusted-public-keys.pks
 */
@Component
@Slf4j
public class ScoringAction implements Action<ScoringAction.ScoringContext,
        Boolean> {
    private static final String LOAN_PRODUCT = "Creditcard";

    @Resource
    private BaseScoring baseScoring;

    @Resource(name = "applicationId")
    private String APPLICATION_ID;
    @Resource(name = "initiatedChannel")
    private String INITIATED_CHANNEL;
    @Resource(name = "participantNumber")
    private String PARTICIPANT_NUMBER;
    @Resource(name = "systemName")
    private String SYSTEM_NAME;
    @Resource(name = "userId")
    private String USER_ID;
    @Resource(name = "internalScoringConnection")
    private ConnectionSettings connectionSettings;
    @Resource(name = "internalScoringPublicKeySettings")
    private KeyStoreSettings trustedPublicKeySettings;
    @Resource(name = "internalScoringUrlPath")
    private String INTERNALSCORING_URL;

    @PostConstruct
    public void init() {
        baseScoring.init(connectionSettings, trustedPublicKeySettings, INTERNALSCORING_URL);
    }

    @Override
    public Boolean perform(ScoringContext scoringContext) {
        if (scoringContext.getPartyEntity().getDecisionScoreEntities()
                .stream()
                .anyMatch(decisionScoreEntity -> DecisionScoreType.CREDITSCORE.equals(decisionScoreEntity.getType())
                        &&  scoringContext.getRequestId().equals(decisionScoreEntity.getRequestId()))) {
            return true;
        }

        String referenceID = scoringContext.getRequestId();
        ScoringRequest request;
        if (scoringContext.getExternalBureauId()!=null){
            request = mapScoringRequestWithBKRId(scoringContext,referenceID);
        } else {
            request = mapScoringRequestWithoutBKRId(scoringContext,referenceID);
        }
        ScoringResponse response;
        try {
            response = baseScoring.perform(request);
        } catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
            throw new ActivityException(InternalScoringConfiguration.SERVICE_NAME, "Error in InternalScoring, referenceId "+referenceID, e);
        }

        scoringContext.getPartyEntity().getDecisionScoreEntities().add(mapCreditScore(scoringContext, response));

        return true;
    }

    private ScoringRequest mapScoringRequestWithBKRId(ScoringContext scoringContext, String referenceID) {
        ScoringRequest request = new ScoringRequest();
        ScoringRequest.Requestor requestor = new ScoringRequest.Requestor();
        requestor.setApplicationID(APPLICATION_ID);
        requestor.setInitiatedChannel(INITIATED_CHANNEL);
        requestor.setParticipantNumber(PARTICIPANT_NUMBER);
        requestor.setSystemName(SYSTEM_NAME);
        requestor.setUserID(USER_ID);
        request.setLoanProduct(LOAN_PRODUCT);
        request.setReferenceID(referenceID);
        request.setRequestor(requestor);

        ScoringRequest.Applicant applicant = new ScoringRequest.Applicant();
        applicant.setCustomerIdentifier(scoringContext.getCustomerId());

        applicant.setExternalCreditBureauId(scoringContext.getExternalBureauId());
        request.getApplicants().add(applicant);
        return request;
    }

    private ScoringRequest mapScoringRequestWithoutBKRId(ScoringContext scoringContext, String referenceID) {
        ScoringRequest request = new ScoringRequest();
        ScoringRequest.Requestor requestor = new ScoringRequest.Requestor();
        requestor.setApplicationID(APPLICATION_ID);
        requestor.setInitiatedChannel(INITIATED_CHANNEL);
        requestor.setParticipantNumber(PARTICIPANT_NUMBER);
        requestor.setSystemName(SYSTEM_NAME);
        requestor.setUserID(USER_ID);
        request.setLoanProduct(LOAN_PRODUCT);
        request.setReferenceID(referenceID);
        request.setRequestor(requestor);

        ScoringRequest.Applicant applicant = new ScoringRequest.Applicant();
        applicant.setCustomerIdentifier(scoringContext.getCustomerId());

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        DateTime dateOfBirth = formatter.parseDateTime(scoringContext.getIndividualsAll().getBirthDate());
        formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        applicant.setDateOfBirth(formatter.print(dateOfBirth));
        applicant.setLastName(scoringContext.getIndividualsAll().getLastName());
        ScoringRequest.PostalAddress postalAddress = new ScoringRequest.PostalAddress();
        postalAddress.setPostalCode(scoringContext.getIndividualsAll().getOfficialAddress().getPostalCode());
        postalAddress.setHouseNumber(scoringContext.getIndividualsAll().getOfficialAddress().getResidenceNumber());
        applicant.setPostalAddress(postalAddress);
        request.getApplicants().add(applicant);
        return request;
    }

    private DecisionScoreEntity mapCreditScore(ScoringContext scoringContext, ScoringResponse response) {
        DecisionScoreEntity creditScoreEntity = new DecisionScoreEntity();
        creditScoreEntity.setType(DecisionScoreType.CREDITSCORE);
        if (scoringContext.getExternalBureauId() != null) {
            creditScoreEntity.setBkrId(scoringContext.getExternalBureauId());
        }
        creditScoreEntity.setPartyId(scoringContext.getCustomerId());
        creditScoreEntity.setDate(new Date());
        creditScoreEntity.setId(UUID.randomUUID().toString());
        creditScoreEntity.setReason(response.getCreditCheckID());

        if (!response.getStatus().getStatusDesc().equals("SUCCESS")) {
            creditScoreEntity.setResult(CreditScoreResult.MISSING);
        } else if (!ScoringDecision.YES.equals(response.getDecision())) {
            creditScoreEntity.setResult(CreditScoreResult.RED);
        } else {
            creditScoreEntity.setResult(CreditScoreResult.GREEN);
        }
        return creditScoreEntity;
    }


    public interface ScoringContext {
        String getCustomerId();
        PartyEntity getPartyEntity();
        IndividualsAllBusinessResponse getIndividualsAll();
        String getExternalBureauId();
        String getRequestId();
    }
}