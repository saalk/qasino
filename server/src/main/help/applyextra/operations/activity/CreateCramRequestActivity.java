package applyextra.operations.activity;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.AbstractActivity;
import applyextra.commons.activity.ActivityOutput;
import applyextra.commons.event.EventOutput;
import applyextra.operations.dto.CramDTO;
import applyextra.operations.event.CreateCramRequestEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@Deprecated // use the CreateCramRequestEvent instead
@Lazy
public class CreateCramRequestActivity extends AbstractActivity<CramDTO> {
    @Resource
    private CreateCramRequestEvent event;

    @Override
    protected ActivityOutput<CramDTO> execution(final Object... objects) {
        final CramDTO inputDto = (CramDTO) objects[0];
        CreateCramRequestEvent.CreateCramRequestEventDTO dto = new CreateCramRequestEvent.CreateCramRequestEventDTO() {
            @Override
            public CramDTO getCramDTO() {
                return inputDto;
            }
        };
        EventOutput output = event.fireEvent(dto);
        return new ActivityOutput<>(output.getResult(), dto.getCramDTO());
    }
}
