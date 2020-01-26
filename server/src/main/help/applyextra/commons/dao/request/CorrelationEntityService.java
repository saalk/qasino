package applyextra.commons.dao.request;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.model.database.constant.CorrelationType;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.util.CorrelationEntityUtil;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CorrelationEntityService {
    /**
     * @deprecated use CorrelationEntityUtil
     */
    @Deprecated
    public void addCorrelation(CreditCardRequestEntity request, String externalReference, CorrelationType correlationType) {
        CorrelationEntityUtil.addCorrelation(request, externalReference, correlationType);
    }

}
