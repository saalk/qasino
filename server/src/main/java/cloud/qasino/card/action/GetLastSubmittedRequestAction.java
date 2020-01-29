package cloud.qasino.card.action;

import cloud.qasino.card.dto.event.EventOutput;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.orchestration.Action;
import cloud.qasino.card.repositories.GameRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Lazy
@Component
public class GetLastSubmittedRequestAction implements Action<GetLastSubmittedRequestAction.GetLastRequestActionDTO, EventOutput> {

    @Resource
    private GameRepository gameRepository;

    @Override
    public EventOutput perform(final GetLastRequestActionDTO flowDto) {
        int userId = flowDto.getCurrentGame().getPlayers().get(0).getUser().getUserId();
        Game request = gameRepository.getLastTypeGameByUser(flowDto.getContextType(), userId);
        flowDto.setLastGame(request);
        return EventOutput.success();
    }

    public interface GetLastRequestActionDTO {
        Game getCurrentGame();

        void setLastGame(final Game request);

        Game getLastRequest();

        Type getContextType();
    }
}
