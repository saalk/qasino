package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.*;
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
public class MapQasinoResponseFromRetrievedDataAction implements Action<MapQasinoResponseFromRetrievedDataAction.Dto, EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        Qasino qasino = new Qasino();

        List<NavigationBarItem> navigationBarItems = new ArrayList<>();
        PageVisitor pageVisitor = new PageVisitor();
        PageGameConfigurator pageGameConfigurator = new PageGameConfigurator();
        PageGamePlay pageGamePlay = new PageGamePlay();
        PagePendingGames pageGamesOverview = new PagePendingGames();
        PageLeagues pageLeagues = new PageLeagues();
        SectionTable table = new SectionTable();

        // 1: PageVisitor
        NavigationBarItem navigationBarItem = new NavigationBarItem();
        navigationBarItem.setItemName("You");
        navigationBarItem.setItemStats("Balance -");
        navigationBarItem.setVisible(false);
        if (actionDto.getQasinoVisitor() != null) {
            actionDto.setShowVisitorPage(true);
            navigationBarItem.setVisible(actionDto.isShowVisitorPage());
            mapVisitorPage(actionDto, navigationBarItem, pageVisitor);
        }
        navigationBarItems.add(navigationBarItem);
        qasino.setPageVisitor(pageVisitor);

        // 2: GameConfigurator
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setItemName("Game");
        navigationBarItem.setItemStats("bots 0 / 0 humans");
        navigationBarItem.setVisible(false);
        if (actionDto.getQasinoGame() != null) {
            actionDto.setShowGameConfigurator(true);
            navigationBarItem.setVisible(actionDto.isShowGameConfigurator());
            mapGameConfiguratorPage(actionDto, navigationBarItem, pageGameConfigurator);
        }
        navigationBarItems.add(navigationBarItem);
        qasino.setPageGameConfigurator(pageGameConfigurator);

        // 3: GamePlay
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setItemName("Qasinogame #-");
        navigationBarItem.setItemStats("move 0/0 round");
        navigationBarItem.setVisible(false);
        if (actionDto.getQasinoGame() != null
                && !(setupGameStates.contains(actionDto.getQasinoGame().getState()))
        ) {
            actionDto.setShowGamePlay(true);
            navigationBarItem.setVisible(actionDto.isShowGamePlay());
            mapGamePlay(actionDto, navigationBarItem, pageGamePlay);
        }
        navigationBarItems.add(navigationBarItem);
        qasino.setPageGamePlay(pageGamePlay);

        // 4: PendingGames
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setItemName("Pending invites - 0");
        navigationBarItem.setItemStats("initiated 0/0 playing");
        navigationBarItem.setVisible(false);
        if (actionDto.getQasinoGame() != null) {
            actionDto.setShowPendingGames(true);
            navigationBarItem.setVisible(actionDto.isShowPendingGames());
            mapPendingGamesPage(actionDto, navigationBarItem, pageGamesOverview);
        }
        navigationBarItems.add(navigationBarItem);
        qasino.setPagePendingGames(pageGamesOverview);

        // 5: Leagues
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setItemName("0 leagues");
        navigationBarItem.setItemStats("0/0 pending/total");
        navigationBarItem.setVisible(false);
        if (actionDto.getQasinoGameLeague() != null) {
            actionDto.setShowLeagues(true);
            navigationBarItem.setVisible(actionDto.isShowLeagues());
            mapLeaguesPage(actionDto, navigationBarItem, pageLeagues);
        }
        navigationBarItems.add(navigationBarItem);
        qasino.setPageLeagues(pageLeagues);

        qasino.setNavBarItems(navigationBarItems);
        qasino.setStatistics(actionDto.getStatistics());
        actionDto.setQasino(qasino);

        return EventOutput.Result.SUCCESS;
    }

    private void mapVisitorPage(Dto actionDto, NavigationBarItem navigationBarItem, PageVisitor pageVisitor) {
        // set the nav bar
        navigationBarItem.setItemName(actionDto.getQasinoVisitor().getVisitorName());
        navigationBarItem.setItemStats("Balance " + actionDto.getQasinoVisitor().getBalance());
        // set the content
        pageVisitor.setSelectedVisitor(actionDto.getQasinoVisitor());
        pageVisitor.setGamesPlayed(0);
        pageVisitor.setGamesWon(0);
        pageVisitor.getGamesPerState();
        pageVisitor.setPendingInvitations(null);
    }
    private void mapGameConfiguratorPage(Dto actionDto, NavigationBarItem navigationBarItem, PageGameConfigurator pageGameConfigurator) {
        // set the nav bar
        navigationBarItem.setItemName("Type " +
                actionDto.getQasinoGame().getType().getLabel());
        long all = 0;
        if (actionDto.getQasinoGamePlayers() != null) {
            pageGameConfigurator.setTotalBotPlayers(
                    (int) actionDto.getQasinoGamePlayers().stream().filter(c -> !c.isHuman()).count());
            pageGameConfigurator.setTotalHumanPlayers(
                    actionDto.getQasinoGamePlayers().size()-pageGameConfigurator.getTotalBotPlayers());
        } else {
            pageGameConfigurator.setTotalBotPlayers(0);
            pageGameConfigurator.setTotalHumanPlayers(0);
        }
        navigationBarItem.setItemStats("bots "+pageGameConfigurator.getTotalBotPlayers() + "/" + pageGameConfigurator.getTotalHumanPlayers() + " humans");
        // set the content
        pageGameConfigurator.setSelectedGame(actionDto.getQasinoGame());
        Style style = Style.fromLabelWithDefault(actionDto.getQasinoGame().getStyle());
        pageGameConfigurator.setAnteToWin(style.getAnteToWin());
        pageGameConfigurator.setBettingStrategy(style.getBettingStrategy());
        pageGameConfigurator.setDeck(style.getDeck());
        pageGameConfigurator.setInsuranceCost(style.getInsuranceCost());
        pageGameConfigurator.setRoundsToWin(style.getRoundsToWin());
        pageGameConfigurator.setTurnsToWin(style.getTurnsToWin());
        pageGameConfigurator.setLeaguesToSelect(null);
    }
    private void mapGamePlay(Dto actionDto, NavigationBarItem navigationBarItem, PageGamePlay pageGamePlay) {
        // set the nav bar
        navigationBarItem.setItemName("Qasinogame#" +
                Integer.toHexString((int) actionDto.getQasinoGame().getGameId()) );
        // set the content
        pageGamePlay.setSelectedGame(actionDto.getQasinoGame());
        pageGamePlay.setGameName(actionDto.getQasinoGame().getType().name());
        if (!(actionDto.getTable() == null)) {
            pageGamePlay.setTable(actionDto.getTable());
      }
        navigationBarItem.setItemStats("move "+ pageGamePlay.getCurrentTurn() + "/" +
                pageGamePlay.getCurrentRound() + " round");
    }

    private void mapPendingGamesPage(Dto actionDto, NavigationBarItem navigationBarItem, PagePendingGames pageGamesOverview) {
        // set the nav bar
        navigationBarItem.setItemName( "Pending Games");
        navigationBarItem.setItemStats("calculate");
        // set the content
        pageGamesOverview.setGamesInitiated(actionDto.getNewGamesForVisitor());
        pageGamesOverview.setGamesWaitingForYouToAccept(null); //todo LOW pending invitations
        pageGamesOverview.setGamesPlayableYouInitiated(null); // todo LOW accepted games
        pageGamesOverview.setGamesYouAccepted(actionDto.getStartedGamesForVisitor());
    }
    private void mapLeaguesPage(Dto actionDto, NavigationBarItem navigationBarItem, PageLeagues pageLeagues) {
        // set the nav bar
        navigationBarItem.setItemName("Leagues "+actionDto.getLeaguesForVisitor().size());
        navigationBarItem.setItemStats("Todo");
        // set the content
        pageLeagues.setSelectedLeague(actionDto.getQasinoGameLeague());
        pageLeagues.setActiveLeagues(actionDto.getLeaguesForVisitor());
        pageLeagues.setResultsForLeague(actionDto.getResultsForLeague());
    }

    private void setErrorMessageCrash(Dto actionDto, String id, String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Entity not found for key" + id);
    }

    public interface Dto {

        // @formatter:off
        // Getters
        Statistics getStatistics();

        boolean isShowVisitorPage();
        boolean isShowGameConfigurator();
        boolean isShowGamePlay();
        boolean isShowLeagues();
        boolean isShowPendingGames();

        Visitor getQasinoVisitor();
        List<League> getLeaguesForVisitor();

        List<Game> getNewGamesForVisitor();
        List<Game> getStartedGamesForVisitor();

        Game getQasinoGame();

        League getQasinoGameLeague();
        List<Result> getResultsForLeague();

        SectionTable getTable();
        List<Player> getQasinoGamePlayers();

        // Setters
        void setQasino(Qasino qasino);

        void setShowVisitorPage(boolean bool);
        void setShowGameConfigurator(boolean bool);
        void setShowGamePlay(boolean bool);
        void setShowPendingGames(boolean bool);
        void setShowLeagues(boolean bool);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
