package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.model.database.entity.ProcessSpecificDataEntity;
import applyextra.commons.orchestration.Action;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Component
@Slf4j
@Lazy
public abstract class PersistProcessSpecificValue
        implements Action<PersistProcessSpecificValue.ProcessSpecificDataActionDTO, Boolean> {

    protected abstract String getKey();

    public Boolean perform(PersistProcessSpecificValue.ProcessSpecificDataActionDTO dto) {
        CreditCardRequestEntity request = dto.getCreditcardRequest();
        ProcessSpecificDataEntity entity = findProcessSpecificData(request);
        if (entity == null) {
            ProcessSpecificDataEntity processSpecificData = this.populateProcessSpecificData(dto);
            log.debug("process specific data is: " + processSpecificData.toString());
            request.getProcessSpecificData()
                    .add(processSpecificData);
        } else {
            entity.setReferenceValue(dto.getProcessSpecificValue(getKey()));
        }
        return Boolean.valueOf(true);
    }

    private ProcessSpecificDataEntity findProcessSpecificData(CreditCardRequestEntity request) {
        Set<ProcessSpecificDataEntity> processSpecificDataEntitySet = request.getProcessSpecificData();
        for (ProcessSpecificDataEntity processSpecificDataEntity : processSpecificDataEntitySet) {
            if (processSpecificDataEntity.getReferenceKey()
                    .equals(this.getKey())) {
                return processSpecificDataEntity;
            }
        }
        return null;
    }

    private ProcessSpecificDataEntity populateProcessSpecificData(ProcessSpecificDataActionDTO flowDTO) {
        CreditCardRequestEntity request = flowDTO.getCreditcardRequest();
        ProcessSpecificDataEntity processSpecificData = new ProcessSpecificDataEntity();
        processSpecificData.setRequest(request);
        processSpecificData.setReferenceKey(getKey());
        processSpecificData.setRequestType(request.getRequestType());
        processSpecificData.setReferenceValue(flowDTO.getProcessSpecificValue(getKey()));
        processSpecificData.setCreationTime(new Date());
        return processSpecificData;
    }

    public interface ProcessSpecificDataActionDTO {
        CreditCardRequestEntity getCreditcardRequest();

        String getProcessSpecificValue(String key);

        void setProcessSpecificValue(String key, String value);
    }
}
