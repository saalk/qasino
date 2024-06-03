package cloud.qasino.games.dto.enums;

import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Params {

    // path params
    public long vid = -1;
    public long gid = -1;
    public String gsg = "";
    public long lid = -1;
    public long ipid = -1;
    public long igid = -1;
    public long apid = -1;
    public long tpid = -1;

    // triggers for the Game
    public GameEvent suppliedGameEvent = GameEvent.ERROR;
    public TurnEvent suppliedTurnEvent = TurnEvent.ERROR;
    public GameStateGroup suppliedGameStateGroup = GameStateGroup.ERROR;

    // Triggers for playing a Game
    public List<PlayingCard> suppliedPlayingCards = Collections.singletonList(PlayingCard.getPlayingCardFromCardId("JR"));   // todo

    List<GameEvent> possibleGameEvents = new ArrayList<>();
    List<TurnEvent> possibleTurnEvents = new ArrayList<>();

}
