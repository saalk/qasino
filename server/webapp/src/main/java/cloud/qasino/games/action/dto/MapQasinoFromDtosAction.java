package cloud.qasino.games.action.dto;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.dto.mapper.GameMapper;
import cloud.qasino.games.dto.mapper.LeagueMapper;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.response.view.NavigationBarItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
            navigationBarItem.setTitle("Visitor[" + qasino.getVisitor().getVisitorId() + "]");
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
            qasino.setActionNeeded(false);
            switch (qasino.getGame().getState().getGroup()) {
                case SETUP, PREPARED -> {
                    qasino.setActionNeeded(true);
                    qasino.setAction(qasino.getGame().getState().getNextAction());
                    navigationBarItem.setTitle("Setup[" +
                            Integer.toHexString((int) qasino.getGame().getGameId()) + "]");
                    long bots = 0;
                    long humans = 0;
                    if (qasino.getGame().getPlayers() != null) {
                        bots = qasino.getGame().getPlayers().stream().filter(c -> !c.isHuman()).count();
                        humans = qasino.getGame().getPlayers().size() - bots;
                    }
                    navigationBarItem.setStat("[" + bots + "/" + humans + "] bots/humans");
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

        if (qasino.getGame() != null) {
            switch (qasino.getGame().getState().getGroup()) {
                case PLAYING, FINISHED -> {
                    qasino.setActionNeeded(true);
                    qasino.setAction(qasino.getGame().getState().getNextAction());
                    navigationBarItem.setTitle("Play[" +
                            Integer.toHexString((int) qasino.getGame().getGameId()) + "]");
                    if (qasino.getPlaying() != null) { // games is still being validated
                        navigationBarItem.setStat(
                                "[" + qasino.getPlaying().getCurrentRoundNumber() +
                                        "/" + qasino.getPlaying().getCurrentSeatNumber() +
                                        "/" + qasino.getPlaying().getCurrentMoveNumber() +
                                        "] round/seat/move");
                    }
                }
            }
        } else {
            if (!qasino.isActionNeeded()) {
                qasino.setActionNeeded(true);
                qasino.setAction("Play a game");
            }
        }
        navigationBarItems.add(navigationBarItem);

        // 4: Nav bar invitations
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setSequence(4);
        navigationBarItem.setTitle("Invite");
        navigationBarItem.setStat("[0/0] total/pending");
        if (qasino.getGame() != null && qasino.getGame().getState().equals(GameState.PENDING_INVITATIONS)) {
            qasino.setActionNeeded(true);
            qasino.setAction(qasino.getGame().getState().getNextAction());
            navigationBarItem.setTitle("Invites[" +
                    Integer.toHexString((int) qasino.getGame().getGameId()) + "]");
            long invitee = 0;
            long invited = 0;
            long rejected = 0;
            if (qasino.getGame().getPlayers() != null) {
                invitee = qasino.getGame().getPlayers().stream().filter(c -> c.getPlayerType() == PlayerType.INVITEE).count();
                invited = qasino.getGame().getPlayers().stream().filter(c -> c.getPlayerType() == PlayerType.INVITED).count();
                rejected = qasino.getGame().getPlayers().stream().filter(c -> c.getPlayerType() == PlayerType.REJECTED).count();
            }
            navigationBarItem.setStat("[" + invitee + invited + rejected+ "/" + invited + "] total/pending");
        }
        navigationBarItems.add(navigationBarItem);

        // 5: Nav bar leagues
        navigationBarItem = new NavigationBarItem();
        navigationBarItem.setSequence(5);
        navigationBarItem.setTitle("League");
        navigationBarItem.setStat("[0] games");
        navigationBarItem.setVisible(false);
        if (qasino.getLeague() != null) {
            if (!qasino.isActionNeeded()) {
                qasino.setActionNeeded(true);
                if (qasino.getAction().isEmpty()) qasino.setAction("Manage your leagues");
            }
            navigationBarItem.setTitle("League[" + Integer.toHexString((int) qasino.getLeague().getLeagueId()) + "]");
            navigationBarItem.setStat("[" + qasino.getLeague().getGamesForLeague().size() + "] games");
        }
        navigationBarItems.add(navigationBarItem);
        qasino.setNavBarItems(navigationBarItems);

        return EventOutput.Result.SUCCESS;
    }
}
