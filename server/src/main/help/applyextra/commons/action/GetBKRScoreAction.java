package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.api.parties.individuals.value.IndividualsAllBusinessResponse;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.model.CreditScoreResult;
import applyextra.commons.model.database.entity.CreditArrearEntity;
import applyextra.commons.model.database.entity.DecisionScoreEntity;
import applyextra.commons.model.database.entity.ExistingLoanEntity;
import applyextra.commons.model.database.entity.PartyEntity;
import applyextra.commons.orchestration.Action;
import applyextra.commons.orchestration.DecisionScoreType;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.creditscore.getbkrscore1.GetBKRScoreServiceOperationClient;
import nl.ing.sc.creditscore.getbkrscore1.jaxb.generated.AggregatedagreementType;
import nl.ing.sc.creditscore.getbkrscore1.jaxb.generated.CreditAgreementArrearType;
import nl.ing.sc.creditscore.getbkrscore1.jaxb.generated.RequestorType;
import nl.ing.sc.creditscore.getbkrscore1.value.GetBKRScoreBusinessRequest;
import nl.ing.sc.creditscore.getbkrscore1.value.GetBKRScoreBusinessResponse;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
@Slf4j
@Lazy
public class GetBKRScoreAction implements Action<GetBKRScoreAction.GetBKRScoreDTO, Boolean> {
    private static final String SERVICE_NAME = "GETBKRSCORE";

    @Resource
    private GetBKRScoreServiceOperationClient serviceClient;
    @Resource(name = "decisionScoreExpirationTime")
    private Integer EXPIRATION_HOURS_DECISIONSCORE = 0;
    @Resource(name = "applicationId")
    private String APPLICATION_ID;
    @Resource(name = "initiatedChannel")
    private String INITIATED_CHANNEL;
    @Resource(name = "participantNumber")
    private Integer PARTICIPANT_NUMBER;
    @Resource(name = "systemName")
    private String SYSTEM_NAME;
    @Resource(name = "userId")
    private String USER_ID;

    @Override
    public Boolean perform(GetBKRScoreDTO flowDTO) {
        if (flowDTO.getPartyEntity().getDecisionScoreEntities()
                .stream()
                .filter(decisionScoreEntity -> DecisionScoreType.BKRSCORE.equals(decisionScoreEntity.getType())
                        &&  flowDTO.getRequestId().equals(decisionScoreEntity.getRequestId()))
                .findAny().isPresent()) {
            return true;
        }
        GetBKRScoreBusinessRequest request = mapRequest(flowDTO);

        final ServiceOperationTask<GetBKRScoreBusinessResponse> response = serviceClient.execute(request);

        final GetBKRScoreBusinessResponse businessResponse = response.getResponse();
        if (response.getResult().isOk() && businessResponse != null && businessResponse.getDecisionType() != null) {
            flowDTO.getPartyEntity().getDecisionScoreEntities().add(mapDecisionScore(flowDTO, businessResponse));
            flowDTO.getPartyEntity().setExistingLoanEntities(mapExistingLoans(flowDTO, businessResponse));
            flowDTO.setExternalBureauId((businessResponse.getBkrId()));
            return true;
        } else {
            throw new ActivityException(SERVICE_NAME, response.getResult().getError().getErrorCode(),
                    "Could not retrieve BKR score", null); //TODO Look at timeouts?
        }
    }

    public interface GetBKRScoreDTO {
        PartyEntity getPartyEntity();

        String getRequestId();

        String getCustomerId();

        IndividualsAllBusinessResponse getIndividualsAll();

        void setExternalBureauId(String externalBureauId);
    }

    private List<ExistingLoanEntity> mapExistingLoans(GetBKRScoreDTO flowDTO, GetBKRScoreBusinessResponse response) {
        List<ExistingLoanEntity> existingLoanEntities = new ArrayList<>();

        for (AggregatedagreementType agreement : response.getAgreements()) {
            ExistingLoanEntity existingLoanEntity = new ExistingLoanEntity();
            existingLoanEntity.setId(UUID.randomUUID().toString());
            existingLoanEntity.setPartyId(flowDTO.getCustomerId());
            existingLoanEntity.setRequestId(flowDTO.getRequestId());
            existingLoanEntity.setAgreementNumber(agreement.getAgreementNumber());
            existingLoanEntity.setAgreementType(agreement.getAgreementType());
            if (agreement.getCalcFinalDownPaymentDate() != null){
                existingLoanEntity.setCalcFinalDownPaymentDate(agreement.getCalcFinalDownPaymentDate().toDate());
            }
            if (agreement.getFirstDownPaymentDate() != null){
                existingLoanEntity.setFirstDownPaymentDate(agreement.getFirstDownPaymentDate().toDate());
            }
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, EXPIRATION_HOURS_DECISIONSCORE);
            existingLoanEntity.setExpireTime(calendar.getTime());
            if (agreement.getRegistrationDate() != null){
                existingLoanEntity.setRegistrationDate(agreement.getRegistrationDate().toDate());
            }
            existingLoanEntity.setRemark(agreement.getRemark());
            existingLoanEntity.setCreditLimit(agreement.getCreditLimit());
            existingLoanEntity.setHoldership(agreement.getHoldership());
            existingLoanEntity.setCreditArrears(mapCreditArrears(agreement));

            existingLoanEntities.add(existingLoanEntity);
        }

        return existingLoanEntities;
    }

    private static List<CreditArrearEntity> mapCreditArrears(AggregatedagreementType agreement) {
        List<CreditArrearEntity> creditArrearEntities = new ArrayList<>();

        for (CreditAgreementArrearType creditAgreementArrearType : agreement.getCreditAgreementArrear()) {
            CreditArrearEntity creditArrearEntity = new CreditArrearEntity();
            creditArrearEntity.setId(UUID.randomUUID().toString());
            if (creditAgreementArrearType.getStartOrChangeDateArrear() != null){
                creditArrearEntity.setStartOrChangeDateArrear(creditAgreementArrearType.getStartOrChangeDateArrear().toDate());
            }
            creditArrearEntity.setType(creditAgreementArrearType.getTypeName().toString());
            creditArrearEntities.add(creditArrearEntity);
        }

        return creditArrearEntities;
    }

    private DecisionScoreEntity mapDecisionScore(GetBKRScoreDTO flowDTO, GetBKRScoreBusinessResponse response) {
        final DecisionScoreEntity bkrCheck = new DecisionScoreEntity();
        bkrCheck.setId(UUID.randomUUID().toString());
        bkrCheck.setDate(new Date());
        bkrCheck.setPartyId(flowDTO.getCustomerId());
        bkrCheck.setType(DecisionScoreType.BKRSCORE);
        bkrCheck.setResult(CreditScoreResult.fromColor(response.getDecisionType().getDecisionScore()));
        bkrCheck.setReason(response.getDecisionType().getReason());
        bkrCheck.setBkrId(response.getBkrId());
        return bkrCheck;
    }

    private GetBKRScoreBusinessRequest mapRequest(GetBKRScoreDTO flowDTO) {
        final GetBKRScoreBusinessRequest request = new GetBKRScoreBusinessRequest();
        request.setRequestor(mapRequestor());
        request.setPartyId(flowDTO.getCustomerId());
        final String bornDate = flowDTO.getIndividualsAll().getBirthDate();
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        final DateTime formattedBornDate = dateFormatter.parseDateTime(bornDate);
        request.setBirthDate(formattedBornDate);
        request.setPostalCode(flowDTO.getIndividualsAll().getOfficialAddress().getPostalCode());
        request.setHouseNumber(flowDTO.getIndividualsAll().getOfficialAddress().getResidenceNumber());
        request.setHouseNumberAddition(flowDTO.getIndividualsAll().getOfficialAddress().getResidenceNumberAddition());
        request.setCountryCode(flowDTO.getIndividualsAll().getOfficialAddress().getIsoCountryCode());
        request.setLastName(flowDTO.getIndividualsAll().getLastName());
        request.setLastNamePrefix(flowDTO.getIndividualsAll().getLastNamePrefix());
        request.setInitials(flowDTO.getIndividualsAll().getInitials());

        return request;
    }

    private RequestorType mapRequestor() {
        final RequestorType requestor = new RequestorType();
        requestor.setApplicationId(APPLICATION_ID);
        requestor.setInitiatedChannel(INITIATED_CHANNEL);
        requestor.setParticipantNumber(PARTICIPANT_NUMBER);
        requestor.setSystemName(SYSTEM_NAME);
        requestor.setUserId(USER_ID);
        return requestor;
    }

}
