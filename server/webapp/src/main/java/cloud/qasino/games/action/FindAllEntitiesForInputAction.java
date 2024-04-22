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

    @Override
    public EventOutput.Result perform(FindAllEntitiesForInputActionDTO actionDto) {

        long id;
        Pageable pageable = PageRequest.of(actionDto.getSuppliedPage(), actionDto.getSuppliedMaxPerPage());

        id = actionDto.getSuppliedVisitorId();
        if (!(id == 0)) {
            Optional<Visitor> foundVisitor = visitorRepository.findById(Long.parseLong(String.valueOf(id)));
            if (foundVisitor.isPresent()) {
                actionDto.setQasinoVisitor(foundVisitor.get());
                actionDto.addKeyValueToHeader("visitorId", String.valueOf(foundVisitor.get().getVisitorId()));
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
            // todo LOW select if one Game is active
            actionDto.setFinishedGamesForVisitor(gameRepository.findAllFinishedGamesForVisitorWithPage(id,
                    pageable));
//                    ,
//                    Sort.by(
//                            Sort.Order.desc("a.\"created\""))
//            );
            actionDto.setLeaguesForVisitor(leagueRepository.findAllActiveLeagueForVisitorWithPage(id, pageable));
            // todo LOW select if one league is active
        }
        id = actionDto.getSuppliedGameId();
        if (!(id == 0)) {
            Optional<Game> foundGame = gameRepository.findById(Long.parseLong(String.valueOf(id)));
            if (foundGame.isPresent()) {
                actionDto.setQasinoGame(foundGame.get());
                actionDto.setQasinoGamePlayers(foundGame.get().getPlayers());
                actionDto.setActiveTurn(foundGame.get().getTurn());
                actionDto.setCardsInTheGame(foundGame.get().getCards());
                actionDto.setAllCardMovesForTheGame(foundGame.get().getTurn().getCardMoves());
                actionDto.addKeyValueToHeader("gameId", String.valueOf(foundGame.get().getGameId()));

            } else {
                setErrorMessageNotFound(actionDto, "gameId", String.valueOf(id));
                return EventOutput.Result.FAILURE;
            }
        }
        id = actionDto.getAcceptedPlayerId();
        if (!(id == 0)) {
            Optional<Player> foundPlayer = playerRepository.findById(Long.parseLong(String.valueOf(id)));
            if (foundPlayer.isPresent()) {
                actionDto.setAcceptedPlayer(foundPlayer.get());
            } else {
                setErrorMessageNotFound(actionDto, "acceptedPlayerId", String.valueOf(id));
                return EventOutput.Result.FAILURE;
            }
        }
        id = actionDto.getInvitedPlayerId();
        if (!(id == 0)) {
            Optional<Player> foundPlayer = playerRepository.findById(Long.parseLong(String.valueOf(id)));
            if (foundPlayer.isPresent()) {
                actionDto.setInvitedPlayer(foundPlayer.get());
            } else {
                setErrorMessageNotFound(actionDto, "invitedPlayerId", String.valueOf(id));
                return EventOutput.Result.FAILURE;
            }
        }
        id = actionDto.getSuppliedTurnPlayerId();
        if (!(id == 0)) {
            Optional<Player> foundPlayer = playerRepository.findById(Long.parseLong(String.valueOf(id)));
            if (foundPlayer.isPresent()) {
                actionDto.setTurnPlayer(foundPlayer.get());
            } else {
                setErrorMessageNotFound(actionDto, "turnPlayerId", String.valueOf(id));
                return EventOutput.Result.FAILURE;
            }
        }
        id = actionDto.getSuppliedLeagueId();
        if (!(id == 0)) {
            Optional<League> foundLeague =
                    leagueRepository.findById(Long.parseLong(String.valueOf(id)));
            if (foundLeague.isPresent()) {
                actionDto.setQasinoGameLeague(foundLeague.get());
                actionDto.addKeyValueToHeader("leagueId", String.valueOf(foundLeague.get().getLeagueId()));
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
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageNotFound(FindAllEntitiesForInputActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(404);
        actionDto.setKey(id);
        actionDto.setValue(value);
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

        // Setter
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
        void addKeyValueToHeader(String key, String value);
        void setHttpStatus(int status);
        void setKey(String key);
        void setValue(String value);
        void setErrorMessage(String key);
        void prepareResponseHeaders();
        // @formatter:on
    }
}
