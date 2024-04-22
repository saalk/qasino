package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.*;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.dto.elements.NavigationBarItem;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.dto.elements.SectionSeat;
import cloud.qasino.games.dto.elements.SectionTable;
import cloud.qasino.games.dto.elements.*;
import cloud.qasino.games.dto.statistics.Statistics;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cloud.qasino.games.database.entity.enums.game.GameState.setupGameStates;

@Slf4j
@Component
public class MapQasinoResponseFromRetrievedDataAction implements Action<MapQasinoResponseFromRetrievedDataAction.MapQasinoResponseFromRetrievedDataDTO, EventOutput.Result> {


    @Override
    public EventOutput.Result perform(MapQasinoResponseFromRetrievedDataDTO actionDto) {

        PageVisitor pageVisitor = new PageVisitor();
        PageGameConfigurator pageGameConfigurator = new PageGameConfigurator();
        PageGamePlay pageGamePlay = new PageGamePlay();
        PagePendingGames pageGamesOverview = new PagePendingGames();
        PageLeagues pageLeagues = new PageLeagues();

        SectionTable table = new SectionTable();

        // Make the navigation bar
        List<NavigationBarItem> navigationBarItems = new ArrayList<>();

        // 1: PageVisitor
        NavigationBarItem navigationBarItem = new NavigationBarItem();
        if (actionDto.getQasinoVisitor() != null) {
            actionDto.setShowVisitorPage(true);
        }
        navigationBarItem.setVisible(actionDto.isShowVisitorPage());
        if (actionDto.isShowVisitorPage()) {
            // set the nav bar
            navigationBarItem.setItemName(actionDto.getQasinoVisitor().getVisitorName());
            navigationBarItem.setItemStats("Balance " + actionDto.getQasinoVisitor().getBalance());
            // set the content
            pageVisitor.setSelectedVisitor(actionDto.getQasinoVisitor());
            pageVisitor.setTotalAcceptedInvitations(0);
            pageVisitor.setTotalPendingInvitations(0);
            pageVisitor.setTotalNewGames(0);
            pageVisitor.setTotalStartedGames(0);
            pageVisitor.setTotalsFinishedGames(0);
            pageVisitor.setAcceptedInvitations(null);
            pageVisitor.setPendingInvitations(null);
        } else {
            navigationBarItem.setItemName("You");
            navigationBarItem.setItemStats("Balance 0");
        }
        navigationBarItems.add(navigationBarItem);

        // 2: GameConfigurator
        navigationBarItem = new NavigationBarItem();
        if (actionDto.getQasinoGame() != null) {
            actionDto.setShowGameConfigurator(true);
        }
        navigationBarItem.setVisible(actionDto.isShowGameConfigurator());
        if (actionDto.isShowGameConfigurator()) {
            if (!(actionDto.getQasinoGame() == null)) {
                // set the nav bar
                navigationBarItem.setItemName("Type " +
                        actionDto.getQasinoGame().getType().getLabel());
                long all = 0;
                if (actionDto.getQasinoGamePlayers() != null) {
                    pageGameConfigurator.setTotalBots(
                            (int) actionDto.getQasinoGamePlayers().stream().filter(c -> !c.isHuman()).count());
                    all = actionDto.getQasinoGamePlayers().size();
                } else {
                    pageGameConfigurator.setTotalBots(0);
                }
                navigationBarItem.setItemStats(pageGameConfigurator.getTotalBots() + "/" + all + " bots/total");
                // set the content
                pageGameConfigurator.setTotalVisitors((int) (all - pageGameConfigurator.getTotalBots()));
                pageGameConfigurator.setHasAnte(actionDto.getQasinoGame().getAnte()>0);
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
            navigationBarItem.setItemName("Type na");
            navigationBarItem.setItemStats("0/0 bots/total");
        }
        navigationBarItems.add(navigationBarItem);

        // 3: GamePlay
        navigationBarItem = new NavigationBarItem();
        if (actionDto.getQasinoGame() != null
                && !setupGameStates.contains(actionDto.getQasinoGame().getState())
        ) {
            actionDto.setShowGamePlay(true);
        }
        navigationBarItem.setVisible(actionDto.isShowGamePlay());
        if (actionDto.isShowGamePlay()) {
            if (!(actionDto.getQasinoGame() == null)) {
                // set the nav bar
                navigationBarItem.setItemName("Qasinogame#" +
                        Integer.toHexString((int) actionDto.getQasinoGame().getGameId()) );
                navigationBarItem.setItemStats(pageGamePlay.getCurrentMove() + "/" +
                        pageGamePlay.getCurrentRound() + " move/round");
                // set the content
                pageGamePlay.setTable(setTable(actionDto));
                pageGamePlay.setCurrentRound(
                        (int) actionDto.getActiveTurn().getCurrentRoundNumber());
                pageGamePlay.setCurrentMove(
                        (int) actionDto.getActiveTurn().getCurrentMoveNumber());
                pageGamePlay.setVisitorPlaysAGame(actionDto.isShowGamePlay());
                pageGamePlay.setSelectedGame(actionDto.getQasinoGame());
                pageGamePlay.setActiveTurn(actionDto.getActiveTurn());
            }
        } else {
            navigationBarItem.setItemName("Qasinogame #-");
            navigationBarItem.setItemStats("0:0 move:round");
        }
        navigationBarItems.add(navigationBarItem);

        // 4: PendingGames
        navigationBarItem = new NavigationBarItem();
        if (actionDto.getQasinoGame() != null) {
            actionDto.setShowPendingGames(true);
        }
        navigationBarItem.setVisible(actionDto.isShowPendingGames());
        if (actionDto.isShowPendingGames()) {
            if (!(actionDto.getLeaguesForVisitor() == null)) {
                // set the nav bar
                navigationBarItem.setItemName( "Pending Games");
                navigationBarItem.setItemStats("calculate");
                // set the content
                pageGamesOverview.setGamesInitiated(actionDto.getNewGamesForVisitor());
                pageGamesOverview.setGamesWaitingForYouToAccept(null); //todo LOW pending invitations
                pageGamesOverview.setGamesPlayableYouInitiated(null); // todo LOW accepted games
                pageGamesOverview.setGamesYouAccepted(actionDto.getStartedGamesForVisitor());
            }
        } else {
            navigationBarItem.setItemName("Pending - 0");
            navigationBarItem.setItemStats("- enddate");
        }
        navigationBarItems.add(navigationBarItem);

        // 5: Leagues
        navigationBarItem = new NavigationBarItem();
        if (actionDto.getQasinoGameLeague() != null) {
            actionDto.setShowLeagues(true);
        }
        navigationBarItem.setVisible(actionDto.isShowLeagues());
        if (actionDto.isShowLeagues()) {
            if (!(actionDto.getLeaguesForVisitor() == null)) {
                navigationBarItem.setItemName("0 leagues");
                pageLeagues.setActiveLeagues(actionDto.getLeaguesForVisitor());
            }
        } else {
            navigationBarItem.setItemName("0 leagues");
            navigationBarItem.setItemStats("0/0 pending/total");
        }
        navigationBarItems.add(navigationBarItem);

        Qasino qasino = new Qasino();
        qasino.setNavBarItems(navigationBarItems);

        qasino.setPageVisitor(pageVisitor);
        qasino.setPageGameConfigurator(pageGameConfigurator);
        qasino.setPageGamePlay(pageGamePlay);
        qasino.setPagePendingGames(pageGamesOverview);
        qasino.setPageLeagues(pageLeagues);
        qasino.setStatistics(actionDto.getStatistics());

        actionDto.setQasino(qasino);
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
    private void setErrorMessageCrash(MapQasinoResponseFromRetrievedDataDTO actionDto, String id, String value) {
        actionDto.setHttpStatus(500);
        actionDto.setKey(id);
        actionDto.setValue(value);
        actionDto.setErrorMessage("Entity not found for key" + id);
        actionDto.prepareResponseHeaders();
    }

    public interface MapQasinoResponseFromRetrievedDataDTO {

        // @formatter:off
        // Getters
        Statistics getStatistics();

        boolean isShowVisitorPage();
        boolean isShowGameConfigurator();
        boolean isShowGamePlay();
        boolean isShowLeagues();
        boolean isShowPendingGames();

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

        void setShowVisitorPage(boolean bool);
        void setShowGameConfigurator(boolean bool);
        void setShowGamePlay(boolean bool);
        void setShowPendingGames(boolean bool);
        void setShowLeagues(boolean bool);

        // error setters
        void setHttpStatus(int status);
        void setKey(String key);
        void setValue(String value);
        void setErrorMessage(String key);
        void prepareResponseHeaders();
        // @formatter:on
    }
}
