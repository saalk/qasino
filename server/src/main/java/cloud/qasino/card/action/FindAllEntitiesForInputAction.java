package cloud.qasino.card.action;

import cloud.qasino.card.action.interfaces.Action;
import cloud.qasino.card.entity.*;
import cloud.qasino.card.event.EventOutput;
import cloud.qasino.card.repository.GameRepository;
import cloud.qasino.card.statemachine.GameState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class FindAllEntitiesForInputAction implements Action<FindAllEntitiesForInputAction.FindAllEntitiesForInputActionDTO, EventOutput.Result> {

    @Resource
    private GameRepository gameRepository;

    @Override
    public EventOutput.Result perform(FindAllEntitiesForInputActionDTO dto) {

        log.debug("Action: FindAllEntitiesForInputAction");
        //final Game currentGame = dto.getCurrentGame();
        return EventOutput.Result.SUCCESS;
    }

    public interface FindAllEntitiesForInputActionDTO {

        int getSuppliedUserId();
        int getSuppliedGameId();
        int getSuppliedLeagueId();
        int getInvitedPlayerId();
        int getAcceptedPlayerId();
        int getSuppliedTurnPlayerId();

        void setGameUser(User user);
        void setInvitedPlayer(Player player);
        void setAcceptedPlayer(Player player);
        void setTurnPlayer(Player player);
        void setQasinoGame(Game game);
        void setGameLeague(League league);
/*
        private List<Game> initiatedGames;

        private List<Player> qasinoPlayers;
        private Turn qasinoTurn;
        private List<Card> qasinoCards;
        private List<CardMove> qasinoCardMoves;

        private List<League> leaguesForUser;
        private List<Result> resultsForLeague;

        private List<User> friends;*/
    }
}
