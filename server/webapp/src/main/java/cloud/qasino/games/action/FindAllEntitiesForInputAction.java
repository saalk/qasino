package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.repository.VisitorRepository;
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
public class FindAllEntitiesForInputAction implements Action<FindAllEntitiesForInputAction.Dto, EventOutput.Result> {

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
    public EventOutput.Result perform(Dto actionDto) {

        visitorId = actionDto.getSuppliedVisitorId();
        leagueId = actionDto.getSuppliedLeagueId();
        turnPlayerId = actionDto.getSuppliedTurnPlayerId();

        if (!(visitorId == 0)) {
            EventOutput.Result response = getVisitorResult(actionDto, visitorId);
            if (response.equals(EventOutput.Result.FAILURE)) return response;
            if (gameId == 0) {
                response = getGameByVisitor(actionDto, visitorId);
                if (response.equals(EventOutput.Result.FAILURE)) return response;
            }
        }
        gameId = actionDto.getSuppliedGameId();

        if (!(leagueId == 0)) {
            EventOutput.Result response = getLeagueResult(actionDto, leagueId);
            if (response.equals(EventOutput.Result.FAILURE)) return response;
        }
        if (!(gameId == 0)) {
            EventOutput.Result response = getGameResult(actionDto, gameId);
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

    private EventOutput.Result getLeagueResult(Dto actionDto, long id) {
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
            setErrorMessageNotFound(actionDto, "leagueId", String.valueOf(id), "League");
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private EventOutput.Result getTurnPlayerResult(Dto actionDto, long id) {
        Optional<Player> foundPlayer = playerRepository.findById(Long.parseLong(String.valueOf(id)));
        if (foundPlayer.isPresent()) {
            gameId = (foundPlayer.get().getGame().getGameId());
            actionDto.setTurnPlayer(foundPlayer.get());
            if (actionDto.getQasinoGamePlayers().size() > 1) {
                // TODO only one round for now do not start with first player again
                Optional<Player> nextPlayer =
                        actionDto.getQasinoGamePlayers()
                                .stream()
                                .filter(p -> p.getSeat() == foundPlayer.get().getSeat() + 1)
                                .findFirst();
                if (nextPlayer.isPresent()) {
                    actionDto.setNextPlayer(nextPlayer.get());
                } else {
                    actionDto.setNextPlayer(null);
                }
            } else {
                // TODO only one round for now do not start with first player again
                actionDto.setNextPlayer(null);
            }
        } else {
            setErrorMessageNotFound(actionDto, "turnPlayerId", String.valueOf(id), "TurnPlayer");
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private EventOutput.Result getGameResult(Dto actionDto, long id) {
        Optional<Game> foundGame = gameRepository.findById(Long.parseLong(String.valueOf(id)));
        if (foundGame.isPresent()) {
            actionDto.setQasinoGame(foundGame.get());
            actionDto.setQasinoGamePlayers(playerRepository.findByGame(actionDto.getQasinoGame()));
            // TODO dont know why this is needed
            actionDto.getQasinoGame().setPlayers(actionDto.getQasinoGamePlayers());
            // when in prepare there are no cards yet so this results in null
            actionDto.setCardsInTheGameSorted(cardRepository.findByGameOrderBySequenceAsc(actionDto.getQasinoGame()));
            actionDto.setActiveTurn(foundGame.get().getTurn());
            if (actionDto.getActiveTurn() != null) {
                actionDto.setAllCardMovesForTheGame(foundGame.get().getTurn().getCardMoves());
            } else {
                // there is no turn yet for the game, but we have a starting and next player
                actionDto.setTurnPlayer(
                        actionDto.getQasinoGamePlayers()
                                .stream()
                                .filter(p -> p.getSeat() == 1)
                                .findFirst().get());
                if (actionDto.getQasinoGamePlayers().size() > 1) {
                    actionDto.setNextPlayer(
                            actionDto.getQasinoGamePlayers()
                                    .stream()
                                    .filter(p -> p.getSeat() == 2)
                                    .findFirst().get());
                } else {
                    actionDto.setNextPlayer(actionDto.getTurnPlayer());
                }
            }


        } else {
            setErrorMessageNotFound(actionDto, "gameId", String.valueOf(id), "Game");
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private EventOutput.Result getGameByVisitor(Dto actionDto, long id) {

        Pageable pageable = PageRequest.of(0, 4
//                Sort.by(
//                        Sort.Order.desc("created"))
        );
        List<Game> foundGame = gameRepository.findAllNewGamesForVisitorWithPage(id, pageable);
        if (foundGame.isEmpty()) {
            foundGame = gameRepository.findAllStartedGamesForVisitorWithPage(id, pageable);
        }
        if (foundGame.isEmpty()) {
            foundGame = gameRepository.findAllFinishedGamesForVisitorWithPage(id, pageable);
            // no games are present for the visitor
            if (foundGame.isEmpty()) return EventOutput.Result.SUCCESS;
        }

        // TODO we found a game - lets just take the first
        actionDto.setQasinoGame(foundGame.get(0));
        actionDto.setQasinoGamePlayers(playerRepository.findByGame(actionDto.getQasinoGame()));
        // TODO dont know why this is needed
        actionDto.getQasinoGame().setPlayers(actionDto.getQasinoGamePlayers());
        // when in prepare there are no cards yet so this results in null
        actionDto.setCardsInTheGameSorted(cardRepository.findByGameOrderBySequenceAsc(actionDto.getQasinoGame()));
        actionDto.setActiveTurn(foundGame.get(0).getTurn());
        actionDto.setSuppliedGameId(foundGame.get(0).getGameId());
        if (actionDto.getActiveTurn() != null) {
            actionDto.setAllCardMovesForTheGame(foundGame.get(0).getTurn().getCardMoves());
            actionDto.setSuppliedTurnPlayerId(actionDto.getActiveTurn().getActivePlayerId());
        } else {
            // there is no turn yet for the game, but we have a starting and next player
            actionDto.setTurnPlayer(
                    actionDto.getQasinoGamePlayers()
                            .stream()
                            .filter(p -> p.getSeat() == 1)
                            .findFirst().get());
            if (actionDto.getQasinoGamePlayers().size() > 1) {
                actionDto.setNextPlayer(
                        actionDto.getQasinoGamePlayers()
                                .stream()
                                .filter(p -> p.getSeat() == 2)
                                .findFirst().get());
            } else {
                actionDto.setNextPlayer(actionDto.getTurnPlayer());
            }
        }
        return EventOutput.Result.SUCCESS;
    }

    private EventOutput.Result getVisitorResult(Dto actionDto, long id) {
        Pageable pageable;
        Optional<Visitor> foundVisitor = visitorRepository.findById(Long.parseLong(String.valueOf(id)));
        if (foundVisitor.isPresent()) {
            actionDto.setQasinoVisitor(foundVisitor.get());
        } else {
            setErrorMessageNotFound(actionDto, "visitorId", String.valueOf(id), "Visitor");
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

    private void setErrorMessageNotFound(Dto actionDto, String id,
                                         String value, String entity) {
        actionDto.setHttpStatus(404);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("[" + entity + "] not found for id [" + value + "]");
    }

    public interface Dto {

        // @formatter:off
        // Getters
        int getSuppliedPage();
        int getSuppliedMaxPerPage();
        long getSuppliedVisitorId();
        long getSuppliedGameId();
        void setSuppliedGameId(long gameId);
        long getSuppliedLeagueId();
        long getInvitedPlayerId();
        long getAcceptedPlayerId();
        long getSuppliedTurnPlayerId();
        void setSuppliedTurnPlayerId(long turnPlayerId);

        List<League> getLeaguesForVisitor();
        List<Player> getQasinoGamePlayers();

        Game getQasinoGame();
        Turn getActiveTurn();
        Player getTurnPlayer();

        // Setters
        void setQasinoVisitor(Visitor visitor);
        void setInvitedPlayer(Player player);

        void setAcceptedPlayer(Player player);
        void setTurnPlayer(Player player);
        void setNextPlayer(Player player);
        void setNewGamesForVisitor(List<Game> games);

        void setStartedGamesForVisitor(List<Game> games);
        void setFinishedGamesForVisitor(List<Game> games);
        void setQasinoGame(Game game);
        void setQasinoGamePlayers(List<Player> players);
        void setActiveTurn(Turn turn);
        void setCardsInTheGameSorted(List<Card> cards);
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
