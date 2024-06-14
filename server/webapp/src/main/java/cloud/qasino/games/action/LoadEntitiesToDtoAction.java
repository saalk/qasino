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
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
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
 * BR1) Visitor: there should always be a valid Visitor supplied - except during signon !!
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
        visitorId = actionDto.getSuppliedVisitorId();

        EventOutput response;
        if (visitorId > 0) {
            getVisitorSupplied(actionDto, visitorId);
            if (actionDto.getQasinoVisitor() == null) {
                throw new MyNPException("92 getVisitorSupplied","visitorId [" + visitorId + "]");
//                setNotFoundErrorMessage(actionDto, "visitorId", String.valueOf(visitorId), "Visitor");
//                return EventOutput.Result.FAILURE;
            }
            if (gameId <= 0) { // BR2
                findGameByVisitorSupplied(actionDto, visitorId);
            }
        } else { // BR1
//            if (!(actionDto.getSuppliedGameEvent() == GameEvent.LOGON)) {
//                throw new MyNPException("101 getVisitorSupplied","visitorId [" + visitorId + "]");
//            setNotFoundErrorMessage(actionDto, "visitorId", String.valueOf(visitorId), "Visitor");
//            return EventOutput.Result.FAILURE;
//            }
        }

        gameId = actionDto.getSuppliedGameId();
//        log.warn("Errors gameId!!: {}", actionDto.getSuppliedGameId());

        if (gameId > 0) { // BR3 BR4 BR6
            getGameSupplied(actionDto, gameId);
            if (actionDto.getQasinoGame() == null) {
                throw new MyNPException("113 getSuppliedGameId","gameId [" + gameId + "]");
//                setNotFoundErrorMessage(actionDto, "gameId", String.valueOf(gameId), "Game");
//                return EventOutput.Result.FAILURE;
            }
//            actionDto.setSuppliedTurnPlayerId(-1);
        }

        turnPlayerId = actionDto.getSuppliedTurnPlayerId();
//        log.warn("Errors turnPlayerId!!: {}", actionDto.getSuppliedTurnPlayerId());

        if (turnPlayerId > 0) { // BR5
            getTurnPlayerSupplied(actionDto, turnPlayerId);
            if (actionDto.getTurnPlayer() == null) {

                throw new MyNPException("127 getTurnPlayerSupplied","turnPlayerId [" + turnPlayerId + "]");

//                setNotFoundErrorMessage(actionDto, "turnPlayerId", String.valueOf(turnPlayerId), "Turn");
//                return EventOutput.Result.FAILURE;
            }
        }

        leagueId = actionDto.getSuppliedLeagueId();
        if (leagueId > 0) {
            getLeagueSupplied(actionDto, leagueId);
            if (actionDto.getQasinoGameLeague() == null) {
                throw new MyNPException("138 getSuppliedLeagueId","leagueId [" + leagueId + "]");

//                setNotFoundErrorMessage(actionDto, "leagueId", String.valueOf(leagueId), "League");
//                return EventOutput.Result.FAILURE;
            }
        }

        // BR7
        invitedVisitorId = actionDto.getAcceptedPlayerId();
        if (invitedVisitorId > 0) {
            Optional<Visitor> foundVisitor = visitorRepository.findById(Long.parseLong(String.valueOf(invitedVisitorId)));
            if (foundVisitor.isPresent()) {
                actionDto.setInvitedVisitor(foundVisitor.get());
            } else {
                throw new MyNPException("152 getAcceptedPlayerId","invitedVisitorId [" + invitedVisitorId + "]");

//                setNotFoundErrorMessage(actionDto, "invitedVisitorId", String.valueOf(invitedVisitorId), "InvitedVisitor");
//                return EventOutput.Result.FAILURE;
           }
        }

        acceptedPlayerId = actionDto.getAcceptedPlayerId();
        if (acceptedPlayerId > 0) {
            Optional<Player> foundPlayer = playerRepository.findById(Long.parseLong(String.valueOf(acceptedPlayerId)));
            if (foundPlayer.isPresent()) {
                actionDto.setAcceptedPlayer(foundPlayer.get());
            } else {
                throw new MyNPException("165 getAcceptedPlayerId","acceptedPlayerId [" + acceptedPlayerId + "]");
//                setNotFoundErrorMessage(actionDto, "acceptedPlayerId", String.valueOf(acceptedPlayerId), "AcceptedPlayer");
//                return EventOutput.Result.FAILURE;
            }
        }

        invitedPlayerId = actionDto.getInvitedPlayerId();
        if (invitedPlayerId > 0) {
            Optional<Player> foundPlayer = playerRepository.findById(Long.parseLong(String.valueOf(invitedPlayerId)));
            if (foundPlayer.isPresent()) {
                actionDto.setInvitedPlayer(foundPlayer.get());
            } else {
                throw new MyNPException("177 getInvitedPlayerId","invitedPlayerId [" + invitedPlayerId + "]");
//                setNotFoundErrorMessage(actionDto, "invitedPlayerId", String.valueOf(invitedPlayerId), "InvitedPlayer");
//                return EventOutput.Result.FAILURE;
            }
        }
        return EventOutput.Result.SUCCESS;
    }
    // @formatter:off
    private void getVisitorSupplied(Dto actionDto, long id) {
        Pageable pageable;
        Optional<Visitor> foundVisitor = visitorRepository.findById(id);
        if (foundVisitor.isPresent()) {
            actionDto.setQasinoVisitor(foundVisitor.get());
        } else {
//            log.warn("Errors id!!: {}", id);
            throw new MyNPException("192 getVisitorSupplied","foundVisitor [" + foundVisitor.toString() + "]");
//            setNotFoundErrorMessage(actionDto, "visitorId", String.valueOf(id), "Visitor");
//            return EventOutput.Result.FAILURE;
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
    private void getGameSupplied(Dto actionDto, long id) {
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
            actionDto.setGameResults(resultsRepository.findByGame(actionDto.getQasinoGame()));

        } else {
            throw new MyNPException("256 getGameSupplied","id [" + id + "]");
//            setNotFoundErrorMessage(actionDto, "gameId", String.valueOf(id), "Game");
//            return EventOutput.Result.FAILURE;
        }
    }
    private void getTurnPlayerSupplied(Dto actionDto, long id) {
        Optional<Player> foundPlayer = playerRepository.findById(id);

        if (!foundPlayer.isEmpty()) {
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

        } else {
            throw new MyNPException("289 getTurnPlayerSupplied","id [" + id + "]");
//            setNotFoundErrorMessage(actionDto, "turnPlayerId", String.valueOf(id), "TurnPlayer");
//            return EventOutput.Result.FAILURE;
        }
    }
    private void getLeagueSupplied(Dto actionDto, long id) {
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
            throw new MyNPException("308 getLeagueSupplied","id [" + id + "]");
//            setNotFoundErrorMessage(actionDto, "leagueId", String.valueOf(id), "League");
//            return EventOutput.Result.FAILURE;
        }
    }

    public EventOutput failure(Dto actionDto) {
        return new EventOutput(EventOutput.Result.FAILURE, actionDto.getSuppliedGameEvent(), actionDto.getSuppliedTurnEvent());
    }
    public EventOutput success(Dto actionDto) {
        return new EventOutput(EventOutput.Result.FAILURE, actionDto.getSuppliedGameEvent(), actionDto.getSuppliedTurnEvent());
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
        GameEvent getSuppliedGameEvent();
        TurnEvent getSuppliedTurnEvent();

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

        Visitor getQasinoVisitor();
        Game getQasinoGame();
        League getQasinoGameLeague();
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
