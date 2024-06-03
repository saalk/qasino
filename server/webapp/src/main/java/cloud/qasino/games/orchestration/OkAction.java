package cloud.qasino.games.orchestration;

import cloud.qasino.games.pattern.statemachine.event.interfaces.AbstractFlowDTO;
import cloud.qasino.games.orchestration.interfaces.StatelessCheck;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
@Component
public class OkAction extends StatelessCheck {
    @Override
    protected boolean check(final AbstractFlowDTO flowDto) {
        return true;
    }
}
