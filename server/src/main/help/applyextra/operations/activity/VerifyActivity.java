package applyextra.operations.activity;

import applyextra.commons.activity.AbstractActivity;
import applyextra.commons.activity.ActivityOutput;
import applyextra.commons.event.EventOutput;
import applyextra.operations.dto.VerifyDTO;
import applyextra.operations.event.VerifyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Deprecated
/**
 * @deprecated use {@link applyextra.operations.event.VerifyEvent} instead
 */
@Lazy
public class VerifyActivity extends AbstractActivity<VerifyDTO> {
	@Resource
	private VerifyEvent event;

	@Override
	protected ActivityOutput<VerifyDTO> execution(final Object... activityInput) {
		final VerifyDTO verifyDTO = (VerifyDTO) activityInput[0];
		final EventOutput eventOutput = event.fireEvent(verifyDTO);
		return new ActivityOutput<>(eventOutput.getResult(), verifyDTO);
	}
}
