package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.dto.request.ParamsDto;
import cloud.qasino.games.dto.request.MessageDto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

//@Data
@Getter
@Setter
@Slf4j
public class QasinoFlowDtoBetter {

    // @formatter:off
    private ParamsDto ids = new ParamsDto();
    private MessageDto message;
    private VisitorDto visitor;
    private GameDto game;
    private InvitationsDTO invitations;
    private LeagueDto league;

    // FOR THE GAME BEING PLAYED
    private Turn activeTurn;
    private Player activePlayer;
    private Player nextPlayer;
    private List<Card> cardsInTheGameSorted;
    private List<CardMove> allCardMovesForTheGame;
    // @formatter:on


}