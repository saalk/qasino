package applyextra.commons.dao.request;

import applyextra.commons.model.database.entity.DecisionScoreEntity;
import applyextra.commons.model.database.entity.ExistingLoanEntity;
import applyextra.commons.model.database.entity.PartyEntity;
import applyextra.commons.model.financialdata.dto.FinancialAcceptanceDTO;
import nl.ing.riaf.core.util.JNDIUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Lazy
public class PartyEntityService {
    @Resource
    private JNDIUtil jndiUtil;
    @Resource
    private PartyEntityRepository repository;
    @Resource
    private DecisionScoreEntityRepository decisionScoreEntityRepository;
    @Resource
    private ExistingLoanEntityRepository existingLoanEntityRepository;

    @Transactional
    public PartyEntity getPartyEntity(String partyId, String requestId) {
        PartyEntity partyEntity = repository.findByPartyId(partyId);
        if (partyEntity == null) {
            partyEntity = new PartyEntity();
            partyEntity.setPartyId(partyId);
        }
        Integer expirationInHours = jndiUtil.getJndiIntValue("param/creditscore/expiration/inhours", true);
        Date expirationDate = new Date(System.currentTimeMillis() - expirationInHours * 3600 * 1000);
        Date currentDate = new Date();

        List<DecisionScoreEntity> decisionScoreEntities = decisionScoreEntityRepository.findByPartyIdAndRequestId(partyId, requestId).stream().filter(decisionScoreEntity -> decisionScoreEntity.getDate().after(expirationDate)).collect(Collectors.toList());

        partyEntity.setDecisionScoreEntities(decisionScoreEntities);

        List<ExistingLoanEntity> existingLoanEntities = existingLoanEntityRepository.findByPartyIdAndRequestId(partyId, requestId).stream().filter(existingLoanEntity -> existingLoanEntity.getExpireTime().after(currentDate)).collect(Collectors.toList());

        partyEntity.setExistingLoanEntities(existingLoanEntities);

        return partyEntity;
    }

    @Transactional
    public void persistPartyEntity(FinancialAcceptanceDTO dto) {
        PartyEntity partyEntity = dto.getPartyEntity();
        partyEntity.setLastUpdated(new Date());
        repository.save(partyEntity);
    }


}
