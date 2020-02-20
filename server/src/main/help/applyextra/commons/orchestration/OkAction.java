package applyextra.commons.orchestration;

import applyextra.commons.event.AbstractFlowDTO;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
@Component
public class OkAction extends StatelessCheck {
    @Override
    protected boolean check(final AbstractFlowDTO flowDto) {
        return true;
    }
}
