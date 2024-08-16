package cloud.qasino.games.dto.model;

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
//    private List<CardMove> cardMoves;
    private PlayerDto currentPlayer;
    private PlayerDto nextPlayer;

    // derived
    private List<SeatDto> seats;

    // Normal fields
    private int currentRoundNumber;
    private int currentSeatNumber;
    private int currentMoveNumber;

}


