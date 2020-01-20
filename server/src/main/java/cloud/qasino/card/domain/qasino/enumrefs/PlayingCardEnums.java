package cloud.qasino.card.domain.qasino.enumrefs;

import cloud.qasino.card.entity.enums.player.AiLevel;
import cloud.qasino.card.entity.enums.player.Avatar;
import cloud.qasino.card.entity.enums.playingcard.Face;
import cloud.qasino.card.entity.enums.playingcard.Location;
import cloud.qasino.card.entity.enums.playingcard.Position;
import lombok.Getter;

import java.util.Map;

@Getter
public class PlayingCardEnums {

    Map<String, Location> location = Location.locationMapNoError;
    Map<String, Position> position = Position.positionMapNoError;
    Map<String, Face> face = Face.faceMapNoError;

}
