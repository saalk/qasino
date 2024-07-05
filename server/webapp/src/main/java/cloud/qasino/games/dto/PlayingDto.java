package cloud.qasino.games.dto;

import cloud.qasino.games.cardengine.cardplay.SeatDto;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import lombok.Data;

import java.util.List;

@Data
/*
  The purpose of using this Dto is to separate the internal representation of playing data
  (e.g., in the database or business logic) from the data exposed to
  external clients or systems.
  */
public class PlayingDto {


    private List<SeatDto> seats;

    private LeagueDto league = null;
    private GameDto game = null;


    // FROM PLAYING
    private List<CardMove> allCardMovesForTheGame;
    private int currentRoundNumber;
    private int currentSeatNumber;
    private int currentMoveNumber;

    // FROM PLAYING - ACTIVE AND NEXT PLAYER
    private PlayerDto player;
    private PlayerDto nextPlayer;
    private PlayerType playerType;
    private int fiches;
    private Avatar avatar;
    private String avatarName;
    private AiLevel aiLevel;
    private boolean human;

}


