package applyextra.operations.event;

import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.model.database.entity.ProcessSpecificDataEntity;
import applyextra.commons.orchestration.Action;

import java.util.Set;

/**
 * Created by CL94WQ on 6-9-2017.
 */
public abstract class RetrieveProcessSpecificValueAction implements Action<PersistProcessSpecificValue
        .ProcessSpecificDataActionDTO, Boolean> {

    protected abstract String getKey();

    public Boolean perform(PersistProcessSpecificValue.ProcessSpecificDataActionDTO dto) {

        CreditCardRequestEntity request = dto.getCreditcardRequest();
        ProcessSpecificDataEntity entity = findProcessSpecificData(request);
        if (entity == null) {
            return false;
        } else {
            dto.setProcessSpecificValue(getKey(), entity.getReferenceValue());
            return true;
        }
    }

    private ProcessSpecificDataEntity findProcessSpecificData(CreditCardRequestEntity request) {
        return findProcessSpecificData(request, getKey());
    }

    public static ProcessSpecificDataEntity findProcessSpecificData(CreditCardRequestEntity request, String key) {
        Set<ProcessSpecificDataEntity> processSpecificDataEntitySet = request.getProcessSpecificData();
        for (ProcessSpecificDataEntity processSpecificDataEntity : processSpecificDataEntitySet) {
            if (processSpecificDataEntity.getReferenceKey()
                    .equals(key)) {
                return processSpecificDataEntity;
            }
        }
        return null;
    }
}
