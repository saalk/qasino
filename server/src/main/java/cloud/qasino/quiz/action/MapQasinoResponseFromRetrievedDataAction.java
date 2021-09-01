package cloud.qasino.quiz.action;

import cloud.qasino.quiz.action.interfaces.Action;
import cloud.qasino.quiz.dto.NavigationBarItem;
import cloud.qasino.quiz.dto.Qasino;
import cloud.qasino.quiz.dto.Seat;
import cloud.qasino.quiz.dto.Table;
import cloud.qasino.quiz.dto.navigation.*;
import cloud.qasino.quiz.dto.statistics.Counter;
import cloud.qasino.quiz.entity.*;
import cloud.qasino.quiz.entity.enums.game.Style;
import cloud.qasino.quiz.entity.enums.move.Move;
import cloud.qasino.quiz.entity.enums.player.Role;
import cloud.qasino.quiz.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MapQasinoResponseFromRetrievedDataAction implements Action<MapQasinoResponseFromRetrievedDataAction.MapQasinoResponseFromRetrievedDataDTO, EventOutput.Result> {


    @Override
    public EventOutput.Result perform(MapQasinoResponseFromRetrievedDataDTO actionDto) {

        log.debug("Action: MapQasinoResponseFromRetrievedDataAction");

        List<NavigationBarItem> navigationBarItems = new ArrayList<>();
        NavigationBarItem navigationBarItem;
        NavigationUser navigationUser = new NavigationUser();
        NavigationGame navigationGame = new NavigationGame();
        NavigationQasino navigationQasino = new NavigationQasino();
        NavigationLeague navigationLeague = new NavigationLeague();
        NavigationFriends navigationFriends = new NavigationFriends();
        Table table = new Table();

        // user
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setVisible(actionDto.isLoggedOn());
        if (actionDto.isLoggedOn()) {
            navigationBarItem.setItemName(actionDto.getGameUser().getUserName());
            navigationBarItem.setItemStats("Balance " + actionDto.getGameUser().getBalance());
            navigationUser.setHasLoggedOn(actionDto.isLoggedOn());
            navigationUser.setUser(actionDto.getGameUser());
            navigationUser.setNewGames(actionDto.getNewGamesForUser());
            navigationUser.setPendingInvitation(null); //todo LOW pending invitations
            navigationUser.setAcceptedGames(null); // todo LOW accepted games
            navigationUser.setStartedGames(actionDto.getStartedGamesForUser());
        } else {
            navigationBarItem.setItemName("You");
            navigationBarItem.setItemStats("0");
        }
        navigationBarItems.add(navigationBarItem);

        // game
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setVisible(actionDto.isBalanceNotZero());
        if (actionDto.isBalanceNotZero()) {
            if (!(actionDto.getQasinoGame() == null)) {
                navigationBarItem.setItemName("Type " +
                        actionDto.getQasinoGame().getType().getLabel());
                navigationGame.setTotalBots(
                        (int) actionDto.getQasinoGamePlayers().stream().filter(c -> !c.isHuman()).count());
                long all = actionDto.getQasinoGamePlayers().size();
                navigationGame.setTotalUsers((int) (all - navigationGame.getTotalBots()));
                navigationBarItem.setItemStats(navigationGame.getTotalBots() + "/" + all + " bots/total");

                navigationGame.setHasBalance(actionDto.isBalanceNotZero());
                navigationGame.setGame(actionDto.getQasinoGame());
                navigationGame.setLeagues(actionDto.getLeaguesForUser());

                Style style = Style.fromLabelWithDefault(actionDto.getQasinoGame().getStyle());
                navigationGame.setAnteToWin(style.getAnteToWin());
                navigationGame.setBettingStrategy(style.getBettingStrategy());
                navigationGame.setDeck(style.getDeck());
                navigationGame.setInsuranceCost(style.getInsuranceCost());
                navigationGame.setRoundsToWin(style.getRoundsToWin());
                navigationGame.setTurnsToWin(style.getTurnsToWin());
            }
        } else {
            navigationBarItem.setItemName("Type -");
            navigationBarItem.setItemStats("0/- bots");
        }
        navigationBarItems.add(navigationBarItem);

        // qasino
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setVisible(actionDto.isGamePlayable());
        if (actionDto.isGamePlayable()) {
            if (!(actionDto.getQasinoGame() == null)) {
                navigationBarItem.setItemName("Qasinogame#" +
                        Integer.toHexString( actionDto.getQasinoGame().getGameId()) );
                navigationQasino.setCurrentRound(
                        (int) actionDto.getQasinoGameTurn().getCurrentRoundNumber());
                navigationQasino.setCurrentMove(
                        (int) actionDto.getQasinoGameTurn().getCurrentMoveNumber());
                navigationBarItem.setItemStats(navigationQasino.getCurrentMove() + "/" +
                        navigationQasino.getCurrentRound() + " move/round");

                navigationQasino.setPlayable(actionDto.isGamePlayable());
                navigationQasino.setSelectedGame(actionDto.getQasinoGame());
                navigationQasino.setActiveTurn(actionDto.getQasinoGameTurn());

            }
        } else {
            navigationBarItem.setItemName("Qasinogame #-");
            navigationBarItem.setItemStats("0:0 move:round");
        }
        navigationBarItems.add(navigationBarItem);

        // league
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setVisible(actionDto.isLeaguePresent());
        if (actionDto.isLeaguePresent()) {
            if (!(actionDto.getLeaguesForUser() == null)) {
                navigationBarItem.setItemName( "League " +
                        actionDto.getQasinoGameLeague().getName());
                navigationBarItem.setItemStats(actionDto.getQasinoGameLeague().getEnded() + " enddate");

                navigationLeague.setLeagues(actionDto.getLeaguesForUser());
            }
        } else {
            navigationBarItem.setItemName("League - ");
            navigationBarItem.setItemStats("- enddate");
        }
        navigationBarItems.add(navigationBarItem);

        // friends
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setVisible(actionDto.isFriendsPresent());
        if (actionDto.isFriendsPresent()) {
            if (!(actionDto.getLeaguesForUser() == null)) {
                navigationBarItem.setItemName("0 friends");
                navigationFriends.setTotalFriends(0);
                navigationFriends.setPendingInvites(0);
                navigationFriends.setAcceptedFriends(null);
                navigationFriends.setPendingInvitations(null);
            }
        } else {
            navigationBarItem.setItemName("0 friends");
            navigationBarItem.setItemStats("0/0 pending/total");
        }
        navigationBarItems.add(navigationBarItem);

        Qasino qasino = new Qasino();
        qasino.setNavBarItems(navigationBarItems);
        qasino.setUserData(navigationUser);
        qasino.setGameData(navigationGame);
        qasino.setQasinoData(navigationQasino);
        qasino.setLeagueData(navigationLeague);
        qasino.setFriendsData(navigationFriends);
        qasino.setTable(setTable(actionDto));
        qasino.setCounter(actionDto.getCounter());

        actionDto.setQasino(qasino);
        //
        return EventOutput.Result.SUCCESS;
    }

    private Table setTable(MapQasinoResponseFromRetrievedDataDTO actionDto) {

        Table table = new Table();

        if (!(actionDto.getQasinoGame() == null)) {

            table.setSelectedGame(actionDto.getQasinoGame());
            table.setLastTurn(actionDto.getQasinoGameTurn());
            // todo HIGH new action for calc all possible moves
            // the statement below gives an error
            // table.setPossibleMoves(new ArrayList(Move.moveMapNoError.keySet()));
            List<Quiz> stockNotInHand =
                    actionDto.getQasinoGameQuizs()
                            .stream()
                            .filter(c -> c.getHand() == null)
                            .collect(Collectors.toList());
            table.setStockNotInHand(stockNotInHand);
            table.setTotalVsStockQuizs(stockNotInHand.size() + "/" + actionDto.getQasinoGameQuizs().size() +
                    " stock/total");
            table.setQuizsLeft(stockNotInHand.size());
            table.setSeats(setSeats(actionDto));
        }
        return table;
    }

    private List<Seat> setSeats(MapQasinoResponseFromRetrievedDataDTO actionDto) {

        List<Seat> seats = new ArrayList<>();

        for (Player player : actionDto.getQasinoGamePlayers()) {
            Seat seat = new Seat();
            seat.setSeatId(player.getSeat());
            seat.setActive(player.getPlayerId() == actionDto.getQasinoGameTurn().getActivePlayerId());
            seat.setBot(!player.isHuman());

            seat.setPlayerId(player.getPlayerId());
            seat.setAvatar(player.getAvatar());
            seat.setAiLevel(player.getAiLevel());
            seat.setInitiator(player.getRole() == Role.INITIATOR);

            if (player.isHuman()) {
                seat.setUserId(player.getUser().getUserId());
                seat.setUserName(player.getUser().getUserName());
            } else {
                seat.setUserId(0);
                seat.setUserName(player.getAiLevel().getLabel() + " " + player.getAvatar().getLabel());
            }

            seat.setBalance(player.getFiches());
            seat.setFiches(player.getFiches());

            List<Quiz> hand = player.getQuizs();
            seat.setQuizsInHand(hand);
            List<String> handStrings =
                    hand.stream().map(Quiz::getQuiz).collect(Collectors.toList());
            seat.setStringQuizsInHand("[" + String.join("],[", handStrings) + "]");
        }
        return seats;
    }

    private void setErrorMessageCrash(MapQasinoResponseFromRetrievedDataDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Entity not found for key" + id);
        actionDto.setUriAndHeaders();
    }

    public interface MapQasinoResponseFromRetrievedDataDTO {

        // @formatter:off
        // Getters
        Counter getCounter();

        boolean isLoggedOn();
        boolean isBalanceNotZero();
        boolean isGamePlayable();
        boolean isLeaguePresent();
        boolean isFriendsPresent();

        User getGameUser();
        Player getInvitedPlayer();
        Player getAcceptedPlayer();
        List<League> getLeaguesForUser();

        Player getTurnPlayer();
        List<Game> getNewGamesForUser();
        List<Game> getStartedGamesForUser();

        List<Game> getFinishedGamesForUser();
        Game getQasinoGame();
        League getQasinoGameLeague();
        List<Player> getQasinoGamePlayers();
        Turn getQasinoGameTurn();
        List<Quiz> getQasinoGameQuizs();
        List<QuizMove> getQasinoGameQuizMoves();

        // Setters
        void setQasino(Qasino qasino);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String key);
        void setErrorValue(String value);
        void setErrorMessage(String key);
        void setUriAndHeaders();
        // @formatter:on
    }
}
