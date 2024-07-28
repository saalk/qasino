package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.CardMove;
import lombok.Data;

import java.util.List;

@Data
/*
  The purpose of using this Dto is to separate the internal representation of playing data
  (e.g., in the database or business logic) from the data exposed to
  external clients or systems.
  */
public class PlayingDto {

    // core
    private long playingId;
//    private String updated; // ignore

    // ref
//    private GameDto game;
//    private PlayerDto player;
//    private List<CardMove> cardMoves;

    // derived
//    private LeagueDto league;
//    private PlayerDto nextPlayer;
//    private List<SeatDto> seats;
//    private List<ResultDto> results;

    // Normal fields
    private int currentRoundNumber;
    private int currentSeatNumber;
    private int currentMoveNumber;

}


