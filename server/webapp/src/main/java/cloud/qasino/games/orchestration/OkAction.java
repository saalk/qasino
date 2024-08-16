package cloud.qasino.games.orchestration;

import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.interfaces.AbstractFlowDto;
import cloud.qasino.games.orchestration.interfaces.StatelessCheck;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
@Component
public class OkAction extends StatelessCheck {
    @Override
    protected boolean check(final AbstractFlowDto flowDto) {
        return true;
    }

    @Override
    public EventOutput.Result perform(Qasino qasino) {
        return null;
    }
}
