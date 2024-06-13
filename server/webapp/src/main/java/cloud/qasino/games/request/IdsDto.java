package cloud.qasino.games.request;

import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IdsDto {

    // path ids

    private long suppliedVisitorId;
    private long suppliedGameId;
    private long suppliedLeagueId;

    private long suppliedInvitedVisitorId;
    private long suppliedAcceptedPlayerId;
    private long suppliedDeclinedPlayerId;

}