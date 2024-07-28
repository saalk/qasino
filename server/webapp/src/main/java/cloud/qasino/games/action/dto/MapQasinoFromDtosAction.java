package cloud.qasino.games.action.dto;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.game.style.AnteToWin;
import cloud.qasino.games.database.entity.enums.game.style.BettingStrategy;
import cloud.qasino.games.database.entity.enums.game.style.DeckConfiguration;
import cloud.qasino.games.database.entity.enums.game.style.OneTimeInsurance;
import cloud.qasino.games.database.entity.enums.game.style.RoundsToWin;
import cloud.qasino.games.database.entity.enums.game.style.TurnsToWin;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.InvitationsDto;
import cloud.qasino.games.dto.mapper.GameMapper;
import cloud.qasino.games.dto.mapper.LeagueMapper;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import cloud.qasino.games.response.QasinoResponse;
import cloud.qasino.games.response.view.NavigationBarItem;
import cloud.qasino.games.response.view.PageGamePlay;
import cloud.qasino.games.response.view.PageGameSetup;
import cloud.qasino.games.response.view.PageLeague;
import cloud.qasino.games.response.view.PageVisitor;
import cloud.qasino.games.response.view.SectionTable;
import cloud.qasino.games.response.view.enums.Params;
import cloud.qasino.games.response.view.statistics.Statistic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MapQasinoFromDtosAction extends ActionDto<EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        qasino.setActionNeeded(false);
        qasino.setAction("No suggestions");

        List<NavigationBarItem> navigationBarItems = new ArrayList<>();

        // 1: Nav bar visitor
        NavigationBarItem navigationBarItem = new NavigationBarItem();
        navigationBarItem.setSequence(1);
        navigationBarItem.setTitle("Visitor");
        navigationBarItem.setStat("balance []");

        if (qasino.getVisitor() != null) {
            navigationBarItem.setTitle("Visitor[" + qasino.getVisitor().getVisitorId()+ "]");
            navigationBarItem.setStat("balance [" + qasino.getVisitor().getBalance() + "]");
            if (qasino.getVisitor().getBalance() == 0) {
                qasino.setActionNeeded(true);
                qasino.setAction("Pawn your ship for fiches");
            }
        } else {
            qasino.setActionNeeded(true);
            qasino.setAction("Logon visitor!");
        }
        navigationBarItems.add(navigationBarItem);

        // 2: Nav bar game setup
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setSequence(2);
        navigationBarItem.setTitle("Setup");
        navigationBarItem.setStat("[0/0] bots/humans");

        if (qasino.getGame() != null) {
            switch (qasino.getGame().getState().getGroup()) {
                case SETUP, PREPARED -> {
                    qasino.setActionNeeded(true);
                    qasino.setAction(qasino.getGame().getState().getNextAction());
                    navigationBarItem.setTitle("Setup[" +
                            Integer.toHexString((int) qasino.getGame().getGameId())+"]");
                    long bots = 0;
                    long humans = 0;
//                    if (qasino.getGame().getPlayers() != null) {
//                        bots = qasino.getGame().getPlayers().stream().filter(c -> !c.isHuman()).count();
//                        humans = qasino.getGame().getPlayers().size() - bots;
//                    }
//                    navigationBarItem.setStat("[" + bots + "/" + humans + "] bots/humans");
                }
                case PLAYING -> {
                    qasino.setActionNeeded(false);
                }
                case FINISHED, ERROR -> {
                    qasino.setActionNeeded(false);
                    // dummy game
                    qasino.setGame(GameMapper.INSTANCE.toDto(Game.buildDummy(null, -1),null));
                }
            }
        } else {
            if (!qasino.isActionNeeded()) {
                qasino.setActionNeeded(true);
                qasino.setAction("Start a new game");
            }
        }
        navigationBarItems.add(navigationBarItem);

        // 3: Nav bar game play
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setSequence(3);
        navigationBarItem.setTitle("Play");
        navigationBarItem.setStat("[0/0/0] round/seat/move");

//        if (qasino.getGame() != null) {
//            switch (qasino.getGame().getState().getGroup()) {
//                case PLAYING, FINISHED -> {
//                    qasino.setActionNeeded(true);
//                    qasino.setAction(qasino.getGame().getState().getNextAction());
//                    navigationBarItem.setTitle("Play[" +
//                            Integer.toHexString((int) qasino.getGame().getGameId())+"]");
//                    // TODO FIXXXX .NullPointerException: Cannot invoke "cloud.qasi
//                    //  no.games.database.entity.Playing.getCurrentRoundNumber()" because the return value of "cloud.qasino.games.action.MapQasinoFromDto$Dto.getActivePlaying()" is null
//                    if (qasino.getPlaying() != null) { // games is still being validated
//                        navigationBarItem.setStat(
//                                "[" + qasino.getPlaying().getCurrentRoundNumber() +
//                                        "/" + qasino.getPlaying().getCurrentSeatNumber() +
//                                        "/" + qasino.getPlaying().getCurrentMoveNumber() +
//                                        "] round/seat/move");
//                    }
//                }
//            }
//        } else {
//            if (!qasino.isActionNeeded()) {
//                qasino.setActionNeeded(true);
//                qasino.setAction("Play a game");
//            }
//        }
//        navigationBarItems.add(navigationBarItem);

        // 4: Nav bar invitations
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setSequence(4);
        navigationBarItem.setTitle("Invite");
        navigationBarItem.setStat("[0/0] setup/playing");
//        if (qasino.getGame() != null && qasino.getGame().getState().equals(GameState.PENDING_INVITATIONS)) {
//            qasino.setActionNeeded(true);
//            qasino.setAction(qasino.getGame().getState().getNextAction());
//            navigationBarItem.setTitle("Invites[]");
//            navigationBarItem.setStat("calculating..");
//        }
//        navigationBarItems.add(navigationBarItem);

        // 5: Nav bar leagues
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setSequence(5);
        navigationBarItem.setTitle("League");
        navigationBarItem.setStat("[0] active");
        navigationBarItem.setVisible(false);
//        if (qasino.getLeague() != null) {
//            if (!qasino.isActionNeeded()) {
//                qasino.setActionNeeded(true);
//                qasino.setAction("Manage your leagues");
//            }
//            navigationBarItem.setTitle("League[" + Integer.toHexString((int) qasino.getLeague().getLeagueId())+"]");
//            navigationBarItem.setStat("[0] active");
//        } else {
//            // set up dummy league
//            qasino.setLeague(LeagueMapper.INSTANCE.toDto(League.buildDummy(null,"leagueName")));
//        }
        navigationBarItems.add(navigationBarItem);

        qasino.setNavBarItems(navigationBarItems);

        return EventOutput.Result.SUCCESS;
    }
}
