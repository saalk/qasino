package applyextra.commons.dao.request;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.configuration.RequestType;
import applyextra.commons.model.database.entity.ProcessSpecificDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProcessSpecificDataService {

    @Autowired
    ProcessSpecificDataRepository processSpecificDataRepository;

    public ProcessSpecificDataEntity addProcessSpecificData(ProcessSpecificDataEntity processSpecificData) {
        log.debug("process specific data" + processSpecificData.toString());
        return processSpecificDataRepository.save(processSpecificData);
    }

    public ProcessSpecificDataEntity fetchProcessSpecificData(String requestId, RequestType[] requestType, String referenceKey) {
        return processSpecificDataRepository.findProcessSpecificDataByRequest_IdAndRequestTypeInAndReferenceKey(requestId, requestType, referenceKey);
    }

    public ProcessSpecificDataEntity updateReferenceValue(ProcessSpecificDataEntity processSpecificData) {
        return processSpecificDataRepository.saveAndFlush(processSpecificData);       
    }

}
