package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.Position;
import lombok.Data;

import java.util.List;

@Data
public class CardDto {

    // core
    private long cardId;
//  private String created; // ignore

    // ref
    private long handId;

    // Normal fields
    private String rankSuit;
    private int sequence;
    private Location location;
    private Position position;
    private Face face;

}
