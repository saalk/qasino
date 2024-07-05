package cloud.qasino.games.dto;

import lombok.Data;

@Data
/**
 * The purpose of using this Dto is to separate the internal representation of user data
 * (e.g., in the database or business logic) from the data exposed to
 * external clients or systems.
 * */
public class GamingTableDto {

    // for create and update
    private long gamingTableId;

    private int currentRoundNumber;
    private int currentSeatNumber;
    private int currentMoveNumber;



}


