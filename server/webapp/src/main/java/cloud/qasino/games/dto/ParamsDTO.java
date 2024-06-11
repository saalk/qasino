package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ParamsDTO {

    // path params
    private long suppliedVisitorId;
    private long suppliedGameId;
    private long suppliedLeagueId;

    private long invitedPlayerId;
    private long acceptedPlayerId;
    private long declinedPlayerId;

    private long suppliedTurnPlayerId;

    //    private VisitorEvent suppliedVisitorEvent;
    //    public boolean requestingToRepay = false;
    //    public boolean offeringShipForPawn = false;
    private GameEvent suppliedGameEvent;
    private TurnEvent suppliedTurnEvent;

    // request params
    private List<PlayingCard> suppliedCards; // future
    private int suppliedPage = 1; // default
    private int suppliedMaxPerPage = 4; // default
    private Location suppliedLocation = Location.HAND; // default

}