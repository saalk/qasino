package cloud.qasino.games.dto.request;

import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class IdsDto {

    // Path ids
    private long suppliedVisitorId;
    private long suppliedGameId;
    private long suppliedLeagueId;

    private long suppliedInvitedVisitorId;
    private long suppliedAcceptedPlayerId;
    private long suppliedDeclinedPlayerId;

    // Events
    private QasinoEvent suppliedQasinoEvent = QasinoEvent.ERROR;
    private GameEvent suppliedGameEvent = GameEvent.ERROR;
    private TurnEvent suppliedTurnEvent = TurnEvent.ERROR;

    // Triggers for playing a Game
    public List<PlayingCard> suppliedPlayingCards = Collections.singletonList(PlayingCard.getPlayingCardFromCardId("JR"));   // todo
    List<GameEvent> possibleGameEvents = new ArrayList<>();
    List<TurnEvent> possibleTurnEvents = new ArrayList<>();

}