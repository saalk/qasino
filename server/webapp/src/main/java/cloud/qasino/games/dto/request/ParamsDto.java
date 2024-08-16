package cloud.qasino.games.dto.request;

import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ParamsDto {

    // Path ids - use refresh methods on abstract class
    private long suppliedVisitorId = 0;
    private String suppliedVisitorUsername = "";
    private long suppliedGameId = 0;
    private String suppliedGameStateGroup = "";
    private long suppliedPlayingId = 0;
    private long suppliedLeagueId = 0;

    // TODO all logic for this ids
    private long suppliedInvitedVisitorId = 0;
    private long suppliedAcceptedPlayerId = 0;
    private long suppliedDeclinedPlayerId = 0;

    // Events
    private QasinoEvent suppliedQasinoEvent = QasinoEvent.NONE;
    private GameEvent suppliedGameEvent = GameEvent.NONE;
    private PlayEvent suppliedPlayEvent = PlayEvent.NONE;

    List<QasinoEvent> possibleNextQasinoEvents = new ArrayList<>();
    List<PlayEvent> possibleNextPlayEvents = new ArrayList<>();
    List<GameEvent> possibleNextGameEvents = new ArrayList<>();

    // Triggers for playing a Game
    // TODO dont belong here
//    public List<PlayingCard> suppliedPlayingCards = Collections.singletonList(PlayingCard.getPlayingCardFromCardId("JR"));   // todo
}