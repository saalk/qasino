package applyextra.operations.event;

import lombok.Getter;
import lombok.Setter;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by CL94WQ on 16-11-2017.
 */
@Component
@Lazy
public class MarkRequestAsExceptionProcess extends PersistProcessSpecificValue {

    public static final String MARKED_AS_EXCEPTION_PROCESS = "markedAsExceptionProcess";

    public Boolean perform(PersistProcessSpecificValue.ProcessSpecificDataActionDTO dto) {

        ExceptionProcessMarkDTO epDTO = new ExceptionProcessMarkDTO();
        epDTO.setCreditcardRequest(dto.getCreditcardRequest());
        return super.perform(epDTO);
    }

    @Override
    protected String getKey() {
        return MARKED_AS_EXCEPTION_PROCESS;
    }

    @Getter
    @Setter
    public static class ExceptionProcessMarkDTO implements ProcessSpecificDataActionDTO {
        private CreditCardRequestEntity creditcardRequest;
        private String markedAsExceptionProcess = "true";

        @Override
        public String getProcessSpecificValue(String key) {
            return markedAsExceptionProcess;
        }

        @Override
        public void setProcessSpecificValue(String key, String value) {
            markedAsExceptionProcess = value;
        }
    }
}
