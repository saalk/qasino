package cloud.qasino.card.orchestration;

import cloud.qasino.card.event.interfaces.AbstractFlowDTO;
import cloud.qasino.card.orchestration.interfaces.StatelessCheck;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
@Component
public class OkAction extends StatelessCheck {
    @Override
    protected boolean check(final AbstractFlowDTO flowDto) {
        return true;
    }
}
