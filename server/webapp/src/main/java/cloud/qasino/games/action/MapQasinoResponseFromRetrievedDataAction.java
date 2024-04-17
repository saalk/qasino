package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.*;
import cloud.qasino.games.dto.NavigationBarItem;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.dto.Seat;
import cloud.qasino.games.dto.Table;
import cloud.qasino.games.dto.navigation.*;
import cloud.qasino.games.dto.statistics.Counter;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.event.EventOutput;
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
        NavigationVisitor navigationVisitor = new NavigationVisitor();
        NavigationGame navigationGame = new NavigationGame();
        NavigationQasino navigationQasino = new NavigationQasino();
        NavigationLeague navigationLeague = new NavigationLeague();
        NavigationFriends navigationFriends = new NavigationFriends();
        Table table = new Table();

        // visitor
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setVisible(actionDto.isLoggedOn());
        if (actionDto.isLoggedOn()) {
            navigationBarItem.setItemName(actionDto.getQasinoVisitor().getVisitorName());
            navigationBarItem.setItemStats("Balance " + actionDto.getQasinoVisitor().getBalance());
            navigationVisitor.setHasLoggedOn(actionDto.isLoggedOn());
            navigationVisitor.setVisitor(actionDto.getQasinoVisitor());
            navigationVisitor.setNewGames(actionDto.getNewGamesForVisitor());
            navigationVisitor.setPendingInvitation(null); //todo LOW pending invitations
            navigationVisitor.setAcceptedGames(null); // todo LOW accepted games
            navigationVisitor.setStartedGames(actionDto.getStartedGamesForVisitor());
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
                navigationGame.setTotalVisitors((int) (all - navigationGame.getTotalBots()));
                navigationBarItem.setItemStats(navigationGame.getTotalBots() + "/" + all + " bots/total");

                navigationGame.setHasBalance(actionDto.isBalanceNotZero());
                navigationGame.setGame(actionDto.getQasinoGame());
                navigationGame.setLeagues(actionDto.getLeaguesForVisitor());

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
                        Integer.toHexString((int) actionDto.getQasinoGame().getGameId()) );
                navigationQasino.setCurrentRound(
                        (int) actionDto.getActiveTurn().getCurrentRoundNumber());
                navigationQasino.setCurrentMove(
                        (int) actionDto.getActiveTurn().getCurrentMoveNumber());
                navigationBarItem.setItemStats(navigationQasino.getCurrentMove() + "/" +
                        navigationQasino.getCurrentRound() + " move/round");

                navigationQasino.setPlayable(actionDto.isGamePlayable());
                navigationQasino.setSelectedGame(actionDto.getQasinoGame());
                navigationQasino.setActiveTurn(actionDto.getActiveTurn());

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
            if (!(actionDto.getLeaguesForVisitor() == null)) {
                navigationBarItem.setItemName( "League " +
                        actionDto.getQasinoGameLeague().getName());
                navigationBarItem.setItemStats(actionDto.getQasinoGameLeague().getEnded() + " enddate");

                navigationLeague.setLeagues(actionDto.getLeaguesForVisitor());
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
            if (!(actionDto.getLeaguesForVisitor() == null)) {
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
        qasino.setVisitorData(navigationVisitor);
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
            table.setLastTurn(actionDto.getActiveTurn());
            // todo HIGH new action for calc all possible moves
            // the statement below gives an error
            // table.setPossibleMoves(new ArrayList(Move.moveMapNoError.keySet()));
            List<Card> stockNotInHand =
                    actionDto.getCardsInTheGame()
                            .stream()
                            .filter(c -> c.getHand() == null)
                            .collect(Collectors.toList());
            table.setStockNotInHand(stockNotInHand);
            table.setTotalVsStockCards(stockNotInHand.size() + "/" + actionDto.getCardsInTheGame().size() +
                    " stock/total");
            table.setCardsLeft(stockNotInHand.size());
            table.setSeats(setSeats(actionDto));
        }
        return table;
    }

    private List<Seat> setSeats(MapQasinoResponseFromRetrievedDataDTO actionDto) {

        List<Seat> seats = new ArrayList<>();

        for (Player player : actionDto.getQasinoGamePlayers()) {
            Seat seat = new Seat();
            seat.setSeatId(player.getSeat());
            seat.setActive(player.getPlayerId() == actionDto.getActiveTurn().getActivePlayerId());
            seat.setBot(!player.isHuman());

            seat.setPlayerId(player.getPlayerId());
            seat.setAvatar(player.getAvatar());
            seat.setAiLevel(player.getAiLevel());
            seat.setInitiator(player.getRole() == Role.INITIATOR);

            if (player.isHuman()) {
                seat.setVisitorId(player.getVisitor().getVisitorId());
                seat.setVisitorName(player.getVisitor().getVisitorName());
            } else {
                seat.setVisitorId(0);
                seat.setVisitorName(player.getAiLevel().getLabel() + " " + player.getAvatar().getLabel());
            }

            seat.setBalance(player.getFiches());
            seat.setFiches(player.getFiches());

            List<Card> hand = player.getCards();
            seat.setCardsInHand(hand);
            List<String> handStrings =
                    hand.stream().map(Card::getCard).collect(Collectors.toList());
            seat.setStringCardsInHand("[" + String.join("],[", handStrings) + "]");
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

        Visitor getQasinoVisitor();
        Player getInvitedPlayer();
        Player getAcceptedPlayer();
        List<League> getLeaguesForVisitor();

        Player getTurnPlayer();
        List<Game> getNewGamesForVisitor();
        List<Game> getStartedGamesForVisitor();

        List<Game> getFinishedGamesForVisitor();
        Game getQasinoGame();
        League getQasinoGameLeague();
        List<Player> getQasinoGamePlayers();
        Turn getActiveTurn();
        List<Card> getCardsInTheGame();
        List<CardMove> getAllCardMovesForTheGame();

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
