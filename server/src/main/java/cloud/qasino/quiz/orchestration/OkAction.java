package cloud.qasino.quiz.orchestration;

import cloud.qasino.quiz.event.interfaces.AbstractFlowDTO;
import cloud.qasino.quiz.orchestration.interfaces.StatelessCheck;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
@Component
public class OkAction extends StatelessCheck {
    @Override
    protected boolean check(final AbstractFlowDTO flowDto) {
        return true;
    }
}
