package applyextra.operations.activity;

import applyextra.commons.activity.AbstractActivity;
import applyextra.commons.activity.ActivityOutput;
import applyextra.commons.event.EventOutput;
import applyextra.operations.dto.CramDTO;
import applyextra.operations.dto.CramStatus;
import applyextra.operations.event.SetCramStatusEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Lazy
public class UpdateStatusBackToDraftActivity extends AbstractActivity<CramDTO> {

    @Resource
    private SetCramStatusEvent setCramStatusEvent;

    @Override
    protected ActivityOutput<CramDTO> execution(Object... objects) {
        final CramDTO dto = (CramDTO) objects[0];
        dto.setStatus(CramStatus.DRAFT);
        final EventOutput result = setCramStatusEvent.fireEvent(new SetCramStatusEvent.SetCramStatusEventDTO() {
            @Override
            public CramDTO getCramDTO() {
                return dto;
            }
        });
        return new ActivityOutput<>(result.getResult(), dto);
    }

}
