package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
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
//    private PlayerDto players;
    private int seat;
    private boolean human;
    private PlayerType playerType;
    private int fiches;
    private Avatar avatar;
    private String avatarName;
    private AiLevel aiLevel;
    private boolean winner;

//    private VisitorDto visitor;
    private String username;
    private String alias;
    private int balance;
    private int securedLoan;

//    private GameDto game;
    private long gameId;
    private int ante;
    // league
    private String name;
    private int nameSequence;
    private boolean active;

    // Normal fields
    private Type type;
    private int year;
    private Month month;
    private String week;
    private int weekday;

    private int fichesWon;

}


