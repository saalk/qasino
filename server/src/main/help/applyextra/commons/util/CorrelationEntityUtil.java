package applyextra.commons.util;

import applyextra.commons.model.database.constant.CorrelationType;
import applyextra.commons.model.database.entity.CorrelationEntity;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;

import java.util.Collection;
import java.util.Date;

public class CorrelationEntityUtil {

    public static void addCorrelation(CreditCardRequestEntity request, String externalReference, CorrelationType correlationType) {
        CorrelationEntity correlationEntity = getCorrelationByType(request.getCorrelations(), correlationType);
        if (correlationEntity == null) {
            correlationEntity = new CorrelationEntity();
        }
        correlationEntity.setCorrelationType(correlationType.name());
        correlationEntity.setExternalReference(externalReference);
        correlationEntity.setRequest(request);
        correlationEntity.setCreationTime(new Date());
        request.getCorrelations().add(correlationEntity);
    }

    public static CorrelationEntity getCorrelationByType(Collection<CorrelationEntity> collection, CorrelationType correlationType) {
        for (CorrelationEntity correlation : collection) {
            if (correlationType.name().equals(correlation.getCorrelationType())) {
                return correlation;
            }
        }
        return null;
    }

}
