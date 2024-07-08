package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.enums.game.Type;
import lombok.Data;

import java.time.Month;

@Data
/*
  The purpose of using this Dto is to separate the internal representation of result data
  (e.g., in the database or business logic) from the data exposed to
  external clients or systems.
  */
public class ResultDto {

    // core
    private long resultId;
//    private String created; // ignore

    // ref
    private PlayerDto players;
    private VisitorDto visitor;
    private GameDto game;

    // Normal fields
    private Type type;
    private int year;
    private Month month;
    private String week;
    private int weekday;
    private int fichesWon;

    // derived
    boolean winner;

}


