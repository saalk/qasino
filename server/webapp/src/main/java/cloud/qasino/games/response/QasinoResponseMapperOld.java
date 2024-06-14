package cloud.qasino.games.response;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.dto.QasinoFlowDto;
import cloud.qasino.games.response.view.NavigationBarItem;
import cloud.qasino.games.response.view.PageGameInvitations;
import cloud.qasino.games.response.view.PageGamePlay;
import cloud.qasino.games.response.view.PageGameSetup;
import cloud.qasino.games.response.view.PageLeague;
import cloud.qasino.games.response.view.PageVisitor;
import cloud.qasino.games.response.view.SectionTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class QasinoResponseMapperOld {

    public QasinoResponse map(QasinoFlowDto actionDto) {

        QasinoResponse qasinoResponse = new QasinoResponse();

        List<NavigationBarItem> navigationBarItems = new ArrayList<>();
        PageVisitor pageVisitor = new PageVisitor();
        PageGameSetup pageGameSetup = new PageGameSetup();
        PageGamePlay pageGamePlay = new PageGamePlay();
        PageGameInvitations pageGamesOverview = new PageGameInvitations();
        PageLeague pageLeague = new PageLeague();

        // special for gameplay
        SectionTable table = new SectionTable();
        actionDto.setActionNeeded(false);
        actionDto.setAction("No suggestions");

        // 1: PageVisitor
        NavigationBarItem navigationBarItem = new NavigationBarItem();
        navigationBarItem.setSequence(1);
        navigationBarItem.setTitle("Visitor[]");
        navigationBarItem.setStat("balance[]");

        if (actionDto.getQasinoVisitor() != null) {
            mapVisitorPage(actionDto, navigationBarItem, pageVisitor);
            actionDto.setShowVisitorPage(true);
            if (actionDto.getQasinoVisitor().getBalance() == 0) {
                actionDto.setActionNeeded(true);
                actionDto.setAction("Pawn your ship for fiches.");
            }
        } else {
            actionDto.setShowVisitorPage(false);
            actionDto.setActionNeeded(true);
            actionDto.setAction("Logon visitor!");
        }
        setupMessage(actionDto, navigationBarItem);
        navigationBarItem.setVisible(actionDto.isShowVisitorPage());
        navigationBarItems.add(navigationBarItem);
        qasinoResponse.setPageVisitor(pageVisitor);

        // 2: GameSetup
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setSequence(2);
        navigationBarItem.setTitle("Setup[]");
        navigationBarItem.setStat("[0/0] bots/humans");

        if (actionDto.getQasinoGame() != null) {
            actionDto.setShowGameSetupPage(true);
            switch (actionDto.getQasinoGame().getState().getGroup()) {
                case SETUP, PREPARED -> {
                    actionDto.setActionNeeded(true);
                    actionDto.setShowGameSetupPage(true);
                    actionDto.setActionNeeded(true);
                    actionDto.setAction(actionDto.getQasinoGame().getState().getNextAction());
                    mapGameSetupPage(actionDto, navigationBarItem, pageGameSetup);
                }
                case PLAYING, FINISHED, ERROR -> {
                    actionDto.setActionNeeded(false);
                    actionDto.setShowGameSetupPage(false);
                }
            }
        } else {
            actionDto.setShowGameSetupPage(false);
            if (!actionDto.isActionNeeded()) {
                actionDto.setActionNeeded(true);
                actionDto.setAction("Start a new game.");
            }
        }
        setupMessage(actionDto, navigationBarItem);
        navigationBarItem.setVisible(actionDto.isShowGameSetupPage());
        navigationBarItems.add(navigationBarItem);
        qasinoResponse.setPageGameSetup(pageGameSetup);

        // 3: GamePlay and Results
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setSequence(3);
        navigationBarItem.setTitle("Play[]");
        navigationBarItem.setStat("[0/0] round/move");

        if (actionDto.getQasinoGame() != null) {
            actionDto.setShowGamePlayPage(true);
            switch (actionDto.getQasinoGame().getState().getGroup()) {
                case SETUP, PREPARED, ERROR -> {
                    actionDto.setShowGamePlayPage(false);
                }
                case PLAYING, FINISHED -> {
                    actionDto.setShowGamePlayPage(true);
                    actionDto.setActionNeeded(true);
                    actionDto.setAction(actionDto.getQasinoGame().getState().getNextAction());
                    mapGamePlayPage(actionDto, navigationBarItem, pageGamePlay);
                }
            }
        } else {
            actionDto.setShowGamePlayPage(false);
            if (!actionDto.isActionNeeded()) {
                actionDto.setActionNeeded(true);
                actionDto.setAction("Play a game");
            }
        }
        setupMessage(actionDto, navigationBarItem);
        navigationBarItem.setVisible(actionDto.isShowGamePlayPage());
        navigationBarItems.add(navigationBarItem);
        qasinoResponse.setPageGamePlay(pageGamePlay);

        // 4: GameInvitatinos
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setSequence(4);
        navigationBarItem.setTitle("Invite[]");
        navigationBarItem.setStat("[0/0] setup/playing");
        actionDto.setShowGamePlayPage(false);
        if (actionDto.getQasinoGame() != null && actionDto.getQasinoGame().getState().equals(GameState.PENDING_INVITATIONS)) {
            actionDto.setShowGameInvitationsPage(true);
            actionDto.setActionNeeded(true);
            actionDto.setAction(actionDto.getQasinoGame().getState().getNextAction());
            mapGameInvitationsPage(actionDto, navigationBarItem, pageGamesOverview);
        }
        navigationBarItem.setVisible(actionDto.isShowGameInvitationsPage());
        navigationBarItems.add(navigationBarItem);
        qasinoResponse.setPageGameInvitations(pageGamesOverview);

        // 5: Leagues
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setSequence(5);
        navigationBarItem.setTitle("League[]");
        navigationBarItem.setStat("[0] active");
        navigationBarItem.setVisible(false);
        if (actionDto.getQasinoGameLeague() != null) {
            actionDto.setShowLeaguesPage(true);
            if (!actionDto.isActionNeeded()) {
                actionDto.setActionNeeded(true);
                actionDto.setAction("Manage your leagues");
            }
            mapLeaguesPage(actionDto, navigationBarItem, pageLeague);
        }
        navigationBarItem.setVisible(actionDto.isShowLeaguesPage());
        navigationBarItems.add(navigationBarItem);
        qasinoResponse.setPageLeague(pageLeague);

        qasinoResponse.setNavBarItems(navigationBarItems);
        qasinoResponse.setStatistics(actionDto.getStatistics());
        qasinoResponse.setAction(actionDto.getAction());
        qasinoResponse.setActionNeeded(actionDto.isActionNeeded());

        return qasinoResponse;
    }

    private static void setupMessage(QasinoFlowDto actionDto, NavigationBarItem navigationBarItem) {
        // todo
    }

    // @formatter:off
    private void mapVisitorPage(QasinoFlowDto actionDto, NavigationBarItem navigationBarItem, PageVisitor pageVisitor) {
        // set the nav bar
        navigationBarItem.setTitle("Visitor[" + actionDto.getQasinoVisitor().getUsername()+ "]");
        navigationBarItem.setStat("balance[" + actionDto.getQasinoVisitor().getBalance() + "]");
        pageVisitor.setSelectedVisitor(actionDto.getQasinoVisitor());
        Map<GameState, Integer> gameStateIntegerMap = new HashMap<>();
        for (Game game : actionDto.getInitiatedGamesForVisitor()) {
            Integer j = gameStateIntegerMap.get(game);
            gameStateIntegerMap.put(game.getState(), (j == null) ? 1 : j + 1);
        }
        pageVisitor.setInitiatedGamesPerState(gameStateIntegerMap);
        gameStateIntegerMap = null;
        for (Game game : actionDto.getInvitedGamesForVisitor()) {
            Integer j = gameStateIntegerMap.get(game);
            gameStateIntegerMap.put(game.getState(), (j == null) ? 1 : j + 1);
        }
        pageVisitor.setInvitedGamesPerState(gameStateIntegerMap);
    }
    private void mapGameSetupPage(QasinoFlowDto actionDto, NavigationBarItem navigationBarItem, PageGameSetup pageGameSetup) {
        // set the nav bar
        navigationBarItem.setTitle("Setup[" +
                Integer.toHexString((int) actionDto.getQasinoGame().getGameId())+"]");
        long bots = 0;
        long humans = 0;
        if (actionDto.getQasinoGamePlayers() != null) {
            bots = actionDto.getQasinoGamePlayers().stream().filter(c -> !c.isHuman()).count();
            humans = actionDto.getQasinoGamePlayers().size() - bots;
        }
        navigationBarItem.setStat("[" + bots + "/" + humans + "] bots/humans");
        // set the content
        pageGameSetup.setSelectedGame(actionDto.getQasinoGame());

        Style style = Style.fromLabelWithDefault(actionDto.getQasinoGame().getStyle());
        pageGameSetup.setAnteToWin(style.getAnteToWin());
        pageGameSetup.setBettingStrategy(style.getBettingStrategy());
        pageGameSetup.setDeckConfiguration(style.getDeckConfiguration());
        pageGameSetup.setOneTimeInsurance(style.getOneTimeInsurance());
        pageGameSetup.setRoundsToWin(style.getRoundsToWin());
        pageGameSetup.setTurnsToWin(style.getTurnsToWin());

        pageGameSetup.setLeaguesToSelect(null);
    }
    private void mapGamePlayPage(QasinoFlowDto actionDto, NavigationBarItem navigationBarItem, PageGamePlay pageGamePlay) {
        // set the nav bar
        navigationBarItem.setTitle("Play[" +
                Integer.toHexString((int) actionDto.getQasinoGame().getGameId())+"]");
        // TODO FIXXXX .NullPointerException: Cannot invoke "cloud.qasino.games.database.entity.Turn.getCurrentRoundNumber()" because the return value of "cloud.qasino.games.action.MapQasinoResponseFromDto$Dto.getActiveTurn()" is null
        if (actionDto.getActiveTurn() != null) { // games is still being validated
            navigationBarItem.setStat(
                    "[" + actionDto.getActiveTurn().getCurrentRoundNumber() +
                            "/" + actionDto.getActiveTurn().getCurrentMoveNumber() +
                            "] round/turn");
        }
        // set the content
        pageGamePlay.setSelectedGame(actionDto.getQasinoGame());
        if (!(actionDto.getTable() == null)) {
            pageGamePlay.setTable(actionDto.getTable());
        } else {
            log.warn("tabel is null !!!");
        }
        pageGamePlay.setGameResults(actionDto.getGameResults());
    }
    private void mapGameInvitationsPage(QasinoFlowDto actionDto, NavigationBarItem navigationBarItem, PageGameInvitations pageGamesOverview) {
        // set the nav bar
        navigationBarItem.setTitle("Invite[]");
        navigationBarItem.setStat("calculating...");

        // set the content
        pageGamesOverview.setGameInvitations(null);
    }
    private void mapLeaguesPage(QasinoFlowDto actionDto, NavigationBarItem navigationBarItem, PageLeague pageLeague) {
        // set the nav bar
        navigationBarItem.setTitle("League[" + Integer.toHexString((int) actionDto.getQasinoGameLeague().getLeagueId())+"]");
        navigationBarItem.setStat("[" + actionDto.getLeaguesForVisitor().size() + "] active");
        // set the content
        pageLeague.setSelectedLeague(actionDto.getQasinoGameLeague());
        pageLeague.setActiveLeagues(actionDto.getLeaguesForVisitor());
        pageLeague.setResultsForLeague(actionDto.getResultsForLeague());
    }
    // @formatter:on

}
