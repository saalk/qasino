package cloud.qasino.games.dto.enums;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.statemachine.event.GameEvent;
import cloud.qasino.games.statemachine.event.TurnEvent;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@ToString
public class Params {

    // path params
    public long suppliedVisitorId = -1;
    public boolean requestingToRepay = false;
    public boolean offeringShipForPawn = false;
    public long suppliedGameId = -1;
    public long suppliedLeagueId = -1;
//    public long initiatingPlayerId;
//    public long invitedPlayerId;
//    public long acceptedPlayerId;
public long suppliedTurnPlayerId = -1;

    // triggers for the Game
    public GameEvent suppliedGameEvent = GameEvent.START;
    public TurnEvent suppliedTurnEvent = TurnEvent.HIGHER;
    public GameStateGroup suppliedGameStateGroup = GameStateGroup.SETUP;

    // Triggers for playing a Game
    public Move suppliedMove = Move.HIGHER;
    public List<PlayingCard> suppliedCards = Collections.singletonList(PlayingCard.getPlayingCardFromCardId("JR"));   // todo

    Map<String, GameEvent> gameEventsPossible = GameEvent.gameEventsPossible;
    Map<String, TurnEvent> turnEventsHighLow = TurnEvent.turnEventsHighLow;
//    Map<String, TurnEvent> turnEventsBlackJack = TurnEvent.turnEventsBlackJack;


}
