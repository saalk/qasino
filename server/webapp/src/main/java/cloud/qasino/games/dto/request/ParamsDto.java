package cloud.qasino.games.dto.request;

import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ParamsDto {

    // Path ids - use refresh methods on abstract class
    private long suppliedVisitorId = 0;
    private String suppliedVisitorUsername = "";
    private long suppliedGameId = 0;
    private long suppliedLeagueId = 0;

    // TODO all logic for this ids
    private long suppliedInvitedVisitorId = 0;
    private long suppliedAcceptedPlayerId = 0;
    private long suppliedDeclinedPlayerId = 0;

    // Events
    private QasinoEvent suppliedQasinoEvent = QasinoEvent.ERROR;
    private GameEvent suppliedGameEvent = GameEvent.ERROR;
    private TurnEvent suppliedTurnEvent = TurnEvent.ERROR;

    List<TurnEvent> possibleNextTurnEvents = new ArrayList<>();
    List<GameEvent> possibleNextGameEvents = new ArrayList<>();

    // Triggers for playing a Game
    // TODO dont belong here
//    public List<PlayingCard> suppliedPlayingCards = Collections.singletonList(PlayingCard.getPlayingCardFromCardId("JR"));   // todo
}