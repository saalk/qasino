package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.*;
import cloud.qasino.games.database.repository.*;
import cloud.qasino.games.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class FindAllEntitiesForInputAction implements Action<FindAllEntitiesForInputAction.FindAllEntitiesForInputActionDTO, EventOutput.Result> {

    @Resource
    GameRepository gameRepository;
    @Resource
    VisitorRepository visitorRepository;
    @Resource
    PlayerRepository playerRepository;
    @Resource
    CardRepository cardRepository;
    @Resource
    TurnRepository turnRepository;
    @Resource
    LeagueRepository leagueRepository;
    @Resource
    ResultsRepository resultsRepository;

    long leagueId;
    long gameId;
    long turnPlayerId;
    long visitorId;

    @Override
    public EventOutput.Result perform(FindAllEntitiesForInputActionDTO actionDto) {

        leagueId = actionDto.getSuppliedLeagueId();
        gameId = actionDto.getSuppliedGameId();
        turnPlayerId = actionDto.getSuppliedTurnPlayerId();
        visitorId = actionDto.getSuppliedVisitorId();

        if (!(leagueId == 0)) {
            EventOutput.Result response = getLeagueResult(actionDto, leagueId);
            if (response.equals(EventOutput.Result.FAILURE)) return response;
        }
        if (!(gameId == 0)) {
            EventOutput.Result response = getGameResult(actionDto, gameId);
            if (response.equals(EventOutput.Result.FAILURE)) return response;
        }
        if (!(visitorId == 0)) {
            EventOutput.Result response = getVisitorResult(actionDto, visitorId);
            if (response.equals(EventOutput.Result.FAILURE)) return response;
        }

        if (!(turnPlayerId == 0)) {
            EventOutput.Result response = getTurnPlayerResult(actionDto, turnPlayerId);
            if (response.equals(EventOutput.Result.FAILURE)) return response;
        }
//        Long id = actionDto.getAcceptedPlayerId();
//        if (!(id == 0)) {
//            Optional<Player> foundPlayer = playerRepository.findById(Long.parseLong(String.valueOf(id)));
//            if (foundPlayer.isPresent()) {
//                actionDto.setAcceptedPlayer(foundPlayer.get());
//            } else {
//                setErrorMessageNotFound(actionDto, "acceptedPlayerId", String.valueOf(id));
//                return EventOutput.Result.FAILURE;
//            }
//        }
//        id = actionDto.getInvitedPlayerId();
//        if (!(id == 0)) {
//            Optional<Player> foundPlayer = playerRepository.findById(Long.parseLong(String.valueOf(id)));
//            if (foundPlayer.isPresent()) {
//                actionDto.setInvitedPlayer(foundPlayer.get());
//            } else {
//                setErrorMessageNotFound(actionDto, "invitedPlayerId", String.valueOf(id));
//                return EventOutput.Result.FAILURE;
//            }
//        }
        return EventOutput.Result.SUCCESS;
    }

    private EventOutput.Result getLeagueResult(FindAllEntitiesForInputActionDTO actionDto, long id) {
        Pageable pageable;
        Optional<League> foundLeague =
                leagueRepository.findById(Long.parseLong(String.valueOf(id)));
        if (foundLeague.isPresent()) {
            actionDto.setQasinoGameLeague(foundLeague.get());
            pageable = PageRequest.of(actionDto.getSuppliedPage(), actionDto.getSuppliedMaxPerPage()
//                        ,
//                        Sort.by(Sort.Order.desc("\"created\""))
            );
            actionDto.setResultsForLeague(resultsRepository.findAllResultForLeagueWithPage(id,
                    pageable));
        } else {
            setErrorMessageNotFound(actionDto, "leagueId", String.valueOf(id));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }
    private EventOutput.Result getTurnPlayerResult(FindAllEntitiesForInputActionDTO actionDto, long id) {
        Optional<Player> foundPlayer = playerRepository.findById(Long.parseLong(String.valueOf(id)));
        if (foundPlayer.isPresent()) {
            gameId = (foundPlayer.get().getGame().getGameId());
            actionDto.setTurnPlayer(foundPlayer.get());
        } else {
            setErrorMessageNotFound(actionDto, "turnPlayerId", String.valueOf(id));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }
    private EventOutput.Result getGameResult(FindAllEntitiesForInputActionDTO actionDto, long id) {
        Optional<Game> foundGame = gameRepository.findById(Long.parseLong(String.valueOf(id)));
        if (foundGame.isPresent()) {
            actionDto.setQasinoGame(foundGame.get());
            actionDto.setQasinoGamePlayers(foundGame.get().getPlayers());
            actionDto.setCardsInTheGame(foundGame.get().getCards());
            actionDto.setActiveTurn(foundGame.get().getTurn());
            if (actionDto.getActiveTurn() != null) {
                actionDto.setAllCardMovesForTheGame(foundGame.get().getTurn().getCardMoves());
            }

        } else {
            setErrorMessageNotFound(actionDto, "gameId", String.valueOf(id));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }
    private EventOutput.Result getVisitorResult(FindAllEntitiesForInputActionDTO actionDto, long id) {
        Pageable pageable;
        Optional<Visitor> foundVisitor = visitorRepository.findById(Long.parseLong(String.valueOf(id)));
        if (foundVisitor.isPresent()) {
            actionDto.setQasinoVisitor(foundVisitor.get());
        } else {
            setErrorMessageNotFound(actionDto, "visitorId", String.valueOf(id));
            return EventOutput.Result.FAILURE;
        }
        pageable = PageRequest.of(actionDto.getSuppliedPage(), actionDto.getSuppliedMaxPerPage()
//                    ,
//                    Sort.by(
//                    Sort.Order.asc("a.\"type\""),
//                    Sort.Order.desc("a.\"updated\""))
        );
        actionDto.setNewGamesForVisitor(gameRepository.findAllNewGamesForVisitorWithPage(id,
                pageable));
        actionDto.setStartedGamesForVisitor(gameRepository.findAllStartedGamesForVisitorWithPage(id,
                pageable));
        actionDto.setFinishedGamesForVisitor(gameRepository.findAllFinishedGamesForVisitorWithPage(id,
                pageable));
//                    ,
//                    Sort.by(
//                            Sort.Order.desc("a.\"created\""))
//            );
        actionDto.setLeaguesForVisitor(leagueRepository.findLeaguesForVisitorWithPage(id, pageable));
        return EventOutput.Result.SUCCESS;
    }
    private void setErrorMessageNotFound(FindAllEntitiesForInputActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(404);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Entity not found for key" + value);
    }
    public interface FindAllEntitiesForInputActionDTO {

        // @formatter:off
        // Getters
        int getSuppliedPage();
        int getSuppliedMaxPerPage();
        long getSuppliedVisitorId();
        long getSuppliedGameId();
        long getSuppliedLeagueId();
        long getInvitedPlayerId();
        long getAcceptedPlayerId();
        long getSuppliedTurnPlayerId();

        List<League> getLeaguesForVisitor();
        Turn getActiveTurn();

        // Setters
        void setQasinoVisitor(Visitor visitor);
        void setInvitedPlayer(Player player);
        void setAcceptedPlayer(Player player);
        void setTurnPlayer(Player player);

        void setNewGamesForVisitor(List<Game> games);
        void setStartedGamesForVisitor(List<Game> games);
        void setFinishedGamesForVisitor(List<Game> games);

        void setQasinoGame(Game game);
        void setQasinoGamePlayers(List<Player> players);
        void setActiveTurn(Turn turn);
        void setCardsInTheGame(List<Card> cards);
        void setAllCardMovesForTheGame(List<CardMove> cardMoves);

        void setQasinoGameLeague(League league);
        void setLeaguesForVisitor(List<League> leagues);
        void setResultsForLeague(List<Result> results);

        void setFriends(List<Visitor> visitors);

        // error and response setters
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
