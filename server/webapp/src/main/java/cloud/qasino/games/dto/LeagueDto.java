package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.Game;
import lombok.Data;

import java.util.List;

@Data
/**
 * The purpose of using this Dto is to separate the internal representation of user data
 * (e.g., in the database or business logic) from the data exposed to
 * external clients or systems.
 * */
public class LeagueDto {

    private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
    private static final String EMAIL_MESSAGE = "{email.message}";

    // for create and update
    long leagueId;
    String name;
    int nameSequence;

    long visitorId;
    String visitorUsername;
    String visitorAlias;

    // derived
    boolean active;

    // for view
    List<Game> games;
    String created;
    String ended;

}


