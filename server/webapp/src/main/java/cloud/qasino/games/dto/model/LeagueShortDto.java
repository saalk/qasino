package cloud.qasino.games.dto.model;

import lombok.Data;

@Data
/*
  The purpose of using this Dto is to separate the internal representation of league data
  (e.g., in the database or business logic) from the data exposed to
  external clients or systems.
  */
public class LeagueShortDto {

    // core
    private long leagueId;
//    String created; // ignore

    // ref
//    private VisitorDto visitor; // not in short
//    private List<GameDto> gamesForLeague; // not in short

    // Normal fields
    private String name;
    private int nameSequence;
    private boolean active;
    private String ended;

}


