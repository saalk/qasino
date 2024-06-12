package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ParamsDto {

    // path params
    private long suppliedVisitorId;
    private long suppliedGameId;
    private long suppliedLeagueId;

    private long suppliedInvitedVisitorId;
    private long suppliedAcceptedPlayerId;
    private long suppliedDeclinedPlayerId;

    //    private VisitorEvent suppliedVisitorEvent;
    //    public boolean requestingToRepay = false;
    //    public boolean offeringShipForPawn = false;
    private GameEvent suppliedGameEvent;
    private TurnEvent suppliedTurnEvent;

    // request params
    private List<String> suppliedRankAndSuits; // future
    private int suppliedPage = 1; // default
    private int suppliedMaxPerPage = 4; // default
    private Location suppliedLocation = Location.HAND; // default

}