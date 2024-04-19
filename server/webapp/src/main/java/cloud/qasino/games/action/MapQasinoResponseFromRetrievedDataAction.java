package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.*;
import cloud.qasino.games.dto.elements.NavigationBarItem;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.dto.elements.SectionSeat;
import cloud.qasino.games.dto.elements.SectionTable;
import cloud.qasino.games.dto.elements.*;
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

        PagePendingGames pageGamesOverview = new PagePendingGames();
        PageGameConfigurator pageGameConfigurator = new PageGameConfigurator();
        PageGamePlay pageGamePlay = new PageGamePlay();
        PageLeagues pageLeagues = new PageLeagues();
        PageVisitor pageVisitor = new PageVisitor();
        SectionTable table = new SectionTable();

        // Make the navigation bar
        List<NavigationBarItem> navigationBarItems = new ArrayList<>();

        // 1: you
        NavigationBarItem navigationBarItem = new NavigationBarItem();
        navigationBarItem.setVisible(actionDto.isLoggedOn());
        if (actionDto.isLoggedOn()) {
            navigationBarItem.setItemName(actionDto.getQasinoVisitor().getVisitorName());
            navigationBarItem.setItemStats("Balance " + actionDto.getQasinoVisitor().getBalance());
//            pageGamesOverview.setHasLoggedOn(actionDto.isLoggedOn());
//            pageGamesOverview.setVisitor(actionDto.getQasinoVisitor());
            pageGamesOverview.setGamesInitiated(actionDto.getNewGamesForVisitor());
            pageGamesOverview.setGamesWaitingForYouToAccept(null); //todo LOW pending invitations
            pageGamesOverview.setGamesPlayableYouInitiated(null); // todo LOW accepted games
            pageGamesOverview.setGamesYouAccepted(actionDto.getStartedGamesForVisitor());
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
                pageGameConfigurator.setTotalBots(
                        (int) actionDto.getQasinoGamePlayers().stream().filter(c -> !c.isHuman()).count());
                long all = actionDto.getQasinoGamePlayers().size();
                pageGameConfigurator.setTotalVisitors((int) (all - pageGameConfigurator.getTotalBots()));
                navigationBarItem.setItemStats(pageGameConfigurator.getTotalBots() + "/" + all + " bots/total");

                pageGameConfigurator.setHasBalance(actionDto.isBalanceNotZero());
                pageGameConfigurator.setSelectedGame(actionDto.getQasinoGame());
                pageGameConfigurator.setLeaguesToSelect(actionDto.getLeaguesForVisitor());

                Style style = Style.fromLabelWithDefault(actionDto.getQasinoGame().getStyle());
                pageGameConfigurator.setAnteToWin(style.getAnteToWin());
                pageGameConfigurator.setBettingStrategy(style.getBettingStrategy());
                pageGameConfigurator.setDeck(style.getDeck());
                pageGameConfigurator.setInsuranceCost(style.getInsuranceCost());
                pageGameConfigurator.setRoundsToWin(style.getRoundsToWin());
                pageGameConfigurator.setTurnsToWin(style.getTurnsToWin());
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
                pageGamePlay.setCurrentRound(
                        (int) actionDto.getActiveTurn().getCurrentRoundNumber());
                pageGamePlay.setCurrentMove(
                        (int) actionDto.getActiveTurn().getCurrentMoveNumber());
                navigationBarItem.setItemStats(pageGamePlay.getCurrentMove() + "/" +
                        pageGamePlay.getCurrentRound() + " move/round");

                pageGamePlay.setPlayable(actionDto.isGamePlayable());
                pageGamePlay.setSelectedGame(actionDto.getQasinoGame());
                pageGamePlay.setActiveTurn(actionDto.getActiveTurn());

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

                pageLeagues.setActiveLeagues(actionDto.getLeaguesForVisitor());
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
                pageVisitor.setTotalAcceptedInvitations(0);
//                pageVisitor.setPendingInvites(0);
                pageVisitor.setAcceptedInvitations(null);
                pageVisitor.setPendingInvitations(null);
            }
        } else {
            navigationBarItem.setItemName("0 friends");
            navigationBarItem.setItemStats("0/0 pending/total");
        }
        navigationBarItems.add(navigationBarItem);

        Qasino qasino = new Qasino();
        qasino.setNavBarItems(navigationBarItems);
        qasino.setPagePendingGames(pageGamesOverview);
        qasino.setPageGameConfigurator(pageGameConfigurator);
        qasino.setPageGamePlay(pageGamePlay);
        qasino.setPageLeagues(pageLeagues);
        qasino.setPageVisitor(pageVisitor);
//        qasino.setTable(setTable(actionDto));
        qasino.setCounter(actionDto.getCounter());

        actionDto.setQasino(qasino);
        //
        return EventOutput.Result.SUCCESS;
    }

    private SectionTable setTable(MapQasinoResponseFromRetrievedDataDTO actionDto) {

        SectionTable table = new SectionTable();

        if (!(actionDto.getQasinoGame() == null)) {

//            table.setSelectedGame(actionDto.getQasinoGame());
            table.setCurrentTurn(actionDto.getActiveTurn());
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

    private List<SectionSeat> setSeats(MapQasinoResponseFromRetrievedDataDTO actionDto) {

        List<SectionSeat> seats = new ArrayList<>();

        for (Player player : actionDto.getQasinoGamePlayers()) {
            SectionSeat seat = new SectionSeat();
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
        actionDto.prepareResponseHeaders();
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
        void prepareResponseHeaders();
        // @formatter:on
    }
}
