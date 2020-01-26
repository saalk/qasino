package applyextra.commons.dao.request;

import applyextra.commons.configuration.RequestType;
import applyextra.commons.model.database.entity.ProcessSpecificDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessSpecificDataRepository extends JpaRepository<ProcessSpecificDataEntity, String> {

    public ProcessSpecificDataEntity findProcessSpecificDataByRequest_IdAndRequestTypeInAndReferenceKey(final String requestId,
                                                                                                        final RequestType[] requestType, final String referenceKey);

}
