package cloud.qasino.games.dto;

import lombok.Data;

import java.util.List;

@Data
/*
  The purpose of using this Dto is to separate the internal representation of league data
  (e.g., in the database or business logic) from the data exposed to
  external clients or systems.
  */
public class LeagueDto {

    // core
    private long leagueId;
//    String created; // ignore

    // ref
//    private VisitorDto visitor;
    private List<GameDto> gamesForLeague;

    // Normal fields
    private String name;
    private int nameSequence;
    private boolean active;
//    private String ended;

}


