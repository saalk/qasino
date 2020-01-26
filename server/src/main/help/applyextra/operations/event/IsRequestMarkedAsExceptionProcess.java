package applyextra.operations.event;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by CL94WQ on 16-11-2017.
 */
@Component
@Lazy
public class IsRequestMarkedAsExceptionProcess extends RetrieveProcessSpecificValueAction {
    public Boolean perform(PersistProcessSpecificValue.ProcessSpecificDataActionDTO dto) {

        MarkRequestAsExceptionProcess.ExceptionProcessMarkDTO epDTO = new MarkRequestAsExceptionProcess.ExceptionProcessMarkDTO();
        epDTO.setCreditcardRequest(dto.getCreditcardRequest());
        return super.perform(epDTO) && "true".equals(epDTO.getMarkedAsExceptionProcess());
    }

    @Override
    protected String getKey() {
        return MarkRequestAsExceptionProcess.MARKED_AS_EXCEPTION_PROCESS;
    }

}
