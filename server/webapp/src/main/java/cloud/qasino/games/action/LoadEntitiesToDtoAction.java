package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;


@Slf4j
@Component
/**
 * @param actionDto Validate supplied ids and store objects in Dto for
 * 1) getSuppliedVisitor: Visitor
 * 2) getSuppliedGame: QasinoGame, TurnPlayer, NextTurnPlayer, QasinoGamePlayers, CardsInTheGameSorted
 * 3) getSuppliedTurnPlayer: TurnPlayer (you might be invited to a Game)
 * 4) getSuppliedLeague: League
 *
 * Business rules:
 * BR1) Visitor: there should always be a valid Visitor supplied
 * BR2) Game: if there is no Game supplied automatically find the last Game the Visitor initiated, if any
 * if there is a Game (with or without ActiveTurn) always try to find the:
 * BR3) - ActiveTurn + TurnPlayer: if there is none supplied use Player with seat 1
 * BR4) - list of QasinoGamePlayers and list of CardsInTheGameSorted
 * BR5) - NextTurnPlayer: find Player with seat after TurnPlayer - can be same as TurnPlayer
 * BR6) - League for the Game
 * BR7) todo GameInvitations pending, playing, finished
 *
 * @return Result.SUCCESS or FAILURE (404) when not found
 */
public class LoadEntitiesToDtoAction implements Action<LoadEntitiesToDtoAction.Dto, EventOutput.Result> {

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

    long visitorId;
    long gameId;
    long turnPlayerId;
    long invitedVisitorId;
    long invitedPlayerId;
    long acceptedPlayerId;
    long leagueId;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        gameId = actionDto.getSuppliedGameId();
//        log.warn("Errors gameId!!: {}", actionDto.getSuppliedGameId());

        visitorId = actionDto.getSuppliedVisitorId();
//        log.warn("Errors visitorId!!: {}", actionDto.getSuppliedVisitorId());

        if (visitorId > 0) {
            EventOutput.Result response = getVisitorSupplied(actionDto, visitorId);
            if (response.equals(EventOutput.Result.FAILURE)) {
                setNotFoundErrorMessage(actionDto, "visitorId", String.valueOf(visitorId), "Visitor");
                return response;
            }
            if (gameId <= 0) { // BR2
                findGameByVisitorSupplied(actionDto, visitorId);
            }
        } else { // BR1
            setNotFoundErrorMessage(actionDto, "visitorId", String.valueOf(visitorId), "Visitor");
            return EventOutput.Result.FAILURE;
        }

        gameId = actionDto.getSuppliedGameId();
        if (gameId > 0) { // BR3 BR4 BR6
            EventOutput.Result response = getGameSupplied(actionDto, gameId);
            if (response.equals(EventOutput.Result.FAILURE)) {
                setNotFoundErrorMessage(actionDto, "gameId", String.valueOf(gameId), "Game");
                return response;
            }
//            actionDto.setSuppliedTurnPlayerId(-1);
        }

        turnPlayerId = actionDto.getSuppliedTurnPlayerId();

        if (turnPlayerId > 0) { // BR5
            EventOutput.Result response = getTurnPlayerSupplied(actionDto, turnPlayerId);
            if (response.equals(EventOutput.Result.FAILURE)) {
                setNotFoundErrorMessage(actionDto, "turnPlayerId", String.valueOf(turnPlayerId), "Turn");
                return response;
            }
        }

        leagueId = actionDto.getSuppliedLeagueId();
        if (leagueId > 0) {
            EventOutput.Result response = getLeagueSupplied(actionDto, leagueId);
            if (response.equals(EventOutput.Result.FAILURE)) {
                setNotFoundErrorMessage(actionDto, "leagueId", String.valueOf(leagueId), "League");
                return response;
            }
        }

        // BR7
        invitedVisitorId = actionDto.getAcceptedPlayerId();
        if (invitedVisitorId > 0) {
            Optional<Visitor> foundVisitor = visitorRepository.findById(Long.parseLong(String.valueOf(invitedVisitorId)));
            if (foundVisitor.isPresent()) {
                actionDto.setInvitedVisitor(foundVisitor.get());
            } else {
                setNotFoundErrorMessage(actionDto, "invitedVisitorId", String.valueOf(invitedVisitorId), "InvitedVisitor");
                return EventOutput.Result.FAILURE;
            }
        }

        acceptedPlayerId = actionDto.getAcceptedPlayerId();
        if (acceptedPlayerId > 0) {
            Optional<Player> foundPlayer = playerRepository.findById(Long.parseLong(String.valueOf(acceptedPlayerId)));
            if (foundPlayer.isPresent()) {
                actionDto.setAcceptedPlayer(foundPlayer.get());
            } else {
                setNotFoundErrorMessage(actionDto, "acceptedPlayerId", String.valueOf(acceptedPlayerId), "AcceptedPlayer");
                return EventOutput.Result.FAILURE;
            }
        }

        invitedPlayerId = actionDto.getInvitedPlayerId();
        if (invitedPlayerId > 0) {
            Optional<Player> foundPlayer = playerRepository.findById(Long.parseLong(String.valueOf(invitedPlayerId)));
            if (foundPlayer.isPresent()) {
                actionDto.setInvitedPlayer(foundPlayer.get());
            } else {
                setNotFoundErrorMessage(actionDto, "invitedPlayerId", String.valueOf(invitedPlayerId), "InvitedPlayer");
                return EventOutput.Result.FAILURE;
            }
        }

        return EventOutput.Result.SUCCESS;
    }

    private EventOutput.Result getVisitorSupplied(Dto actionDto, long id) {
        Pageable pageable;
        Optional<Visitor> foundVisitor = visitorRepository.findById(id);
        if (foundVisitor.isPresent()) {
            actionDto.setQasinoVisitor(foundVisitor.get());
        } else {
//            log.warn("Errors id!!: {}", id);
            setNotFoundErrorMessage(actionDto, "visitorId", String.valueOf(id), "Visitor");
            return EventOutput.Result.FAILURE;
        }
        pageable = PageRequest.of(1, 10
//                    ,
//                    Sort.by(
//                    Sort.Order.asc("a.\"type\""),
//                    Sort.Order.desc("a.\"updated\""))
        );
        actionDto.setInitiatedGamesForVisitor(gameRepository.findAllGamesForInitiatorWithPage(id, pageable));
        actionDto.setInvitedGamesForVisitor(gameRepository.findAllInvitedGamesForVisitorWithPage(id, pageable));
//                    ,
//                    Sort.by(
//                            Sort.Order.desc("a.\"created\""))
//            );
        actionDto.setLeaguesForVisitor(leagueRepository.findLeaguesForVisitorWithPage(id, pageable));
        return EventOutput.Result.SUCCESS;
    }
    private void findGameByVisitorSupplied(Dto actionDto, long id) {
        Pageable pageable = PageRequest.of(0, 4);
        List<Game> foundGame = gameRepository.findAllNewGamesForVisitorWithPage(id, pageable);
        if (foundGame.isEmpty()) {
            foundGame = gameRepository.findAllStartedGamesForVisitorWithPage(id, pageable);
        }
        if (foundGame.isEmpty()) {
            foundGame = gameRepository.findAllFinishedGamesForVisitorWithPage(id, pageable);
            // no games are present for the visitor
            if (foundGame.isEmpty()) return;
        }
        // BR 2
        actionDto.setSuppliedGameId(foundGame.get(0).getGameId());
    }
    private EventOutput.Result getGameSupplied(Dto actionDto, long id) {
        Optional<Game> foundGame = gameRepository.findById(Long.parseLong(String.valueOf(id)));
        if (foundGame.isPresent()) {
            actionDto.setQasinoGame(foundGame.get());
            if (actionDto.getSuppliedLeagueId() == 0 && actionDto.getQasinoGame().getLeague() != null ) {
                actionDto.setSuppliedLeagueId(actionDto.getQasinoGame().getLeague().getLeagueId());
            }
            // TODO check which works
            // BR5
            actionDto.setQasinoGamePlayers(playerRepository.findByGame(actionDto.getQasinoGame()));
//            actionDto.setQasinoGamePlayers(foundGame.get().getPlayers());
            // TODO dont know why this is needed
            actionDto.getQasinoGame().setPlayers(actionDto.getQasinoGamePlayers());
            // when in validate there are no cards yet so this results in null
            actionDto.setCardsInTheGameSorted(cardRepository.findByGameOrderBySequenceAsc(actionDto.getQasinoGame()));
            actionDto.setActiveTurn(foundGame.get().getTurn());
            if (actionDto.getActiveTurn() != null) {
                actionDto.setSuppliedTurnPlayerId(actionDto.getActiveTurn().getActivePlayerId());

                actionDto.setAllCardMovesForTheGame(foundGame.get().getTurn().getCardMoves());
            } else {
                // TODO check if this is needed
//                actionDto.setSuppliedTurnPlayerId(
//                        actionDto.getQasinoGamePlayers()
//                                .stream()
//                                .filter(p -> p.getSeat() == 1)
//                                .findFirst().get().getPlayerId());
            }
            actionDto.setGameResults(resultsRepository.findAllByGame(actionDto.getQasinoGame()));

        } else {
            setNotFoundErrorMessage(actionDto, "gameId", String.valueOf(id), "Game");
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }
    private EventOutput.Result getTurnPlayerSupplied(Dto actionDto, long id) {
        Optional<Player> foundPlayer = playerRepository.findById(id);

//        if (!foundPlayer.isEmpty()) {
//            gameId = (foundPlayer.get().getGame().getGameId());
            actionDto.setTurnPlayer(foundPlayer.get());
            // find next player
            int nextSeat = foundPlayer.get().getSeat() + 1;
            if (nextSeat > actionDto.getQasinoGamePlayers().size()) nextSeat = 1;
            if (actionDto.getQasinoGamePlayers().size() > 1) {
                int finalNextSeat = nextSeat;
                Optional<Player> nextPlayer =
                        actionDto.getQasinoGamePlayers()
                                .stream()
                                .filter(p -> p.getSeat() == finalNextSeat)
                                .findFirst();
                if (nextPlayer.isPresent()) {
                    actionDto.setNextPlayer(nextPlayer.get());
                } else {
                    actionDto.setNextPlayer(null);
                }
            } else {
                // this player is also next player
                actionDto.setNextPlayer(actionDto.getTurnPlayer());
            }

//        } else {
//            setNotFoundErrorMessage(actionDto, "turnPlayerId", String.valueOf(id), "TurnPlayer");
//            return EventOutput.Result.FAILURE;
//        }
        return EventOutput.Result.SUCCESS;
    }
    private EventOutput.Result getLeagueSupplied(Dto actionDto, long id) {
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
            setNotFoundErrorMessage(actionDto, "leagueId", String.valueOf(id), "League");
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setNotFoundErrorMessage(Dto actionDto, String id, String value, String entity) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setNotFoundErrorMessage("[" + entity + "] not found for id [" + value + "]");
//        log.warn("Errors setNotFoundErrorMessage!!: {}", actionDto.getErrorMessage());

    }

    public interface Dto {

        // @formatter:off
        String getErrorMessage();

        // Getters
        int getSuppliedPage();
        int getSuppliedMaxPerPage();

        long getSuppliedVisitorId();
        long getSuppliedGameId();
        void setSuppliedGameId(long gameId);
        long getSuppliedLeagueId();
        void setSuppliedLeagueId(long leagueId);
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

        void setInvitedVisitor(Visitor visitor);
        void setInvitedPlayer(Player player);
        void setAcceptedPlayer(Player player);

        void setTurnPlayer(Player player);
        void setNextPlayer(Player player);

        void setInitiatedGamesForVisitor(List<Game> games);
        List<Game> getInitiatedGamesForVisitor();
        void setInvitedGamesForVisitor(List<Game> games);
        List<Game> getInvitedGamesForVisitor();

        void setQasinoGame(Game game);
        void setQasinoGamePlayers(List<Player> players);
        void setActiveTurn(Turn turn);
        void setGameResults(List<Result> results);

        void setCardsInTheGameSorted(List<Card> cards);
        void setAllCardMovesForTheGame(List<CardMove> cardMoves);

        void setQasinoGameLeague(League league);
        void setLeaguesForVisitor(List<League> leagues);
        void setResultsForLeague(List<Result> results);

        // error setters
        // @formatter:off
        void setBadRequestErrorMessage(String problem);
        void setNotFoundErrorMessage(String problem);
        void setConflictErrorMessage(String reason);
        void setUnprocessableErrorMessage(String reason);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        // @formatter:on
    }
}
