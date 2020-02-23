package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static applyextra.commons.resource.ExceptionZappingEmailConstants.*;


@Slf4j
@Component
@Lazy
public class PrepareExceptionProcessEmailAction implements Action<PrepareExceptionProcessEmailAction.PrepareExceptionProcessEmailActionDTO, EventOutput.Result> {

@Override
public EventOutput.Result perform(PrepareExceptionProcessEmailActionDTO dto) {
    final Map<String, Object> data = new HashMap<>();
    if(dto.getPartyId() !=null && dto.getRequestorId() !=null) {
        data.put(CUSTOMER_ID, dto.getPartyId());
        data.put(REQUESTOR_ID, dto.getRequestorId());
        dto.setDataToSend(data);
    } else {
        log.error("The fields necessary for sending the email are not present");
        return EventOutput.Result.FAILURE;
    }

    return EventOutput.Result.SUCCESS;
}

public interface PrepareExceptionProcessEmailActionDTO {

    String getPartyId();

    void setDataToSend(Map<String, Object> data);

    String getRequestorId();

}
}
