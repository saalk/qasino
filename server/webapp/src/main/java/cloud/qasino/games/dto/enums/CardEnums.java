package cloud.qasino.games.dto.enums;

import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.Position;
import lombok.Getter;

import java.util.Map;

@Getter
public class CardEnums {

    Map<String, Location> location = Location.locationMapNoError;
    Map<String, Position> position = Position.positionMapNoError;
    Map<String, Face> face = Face.faceMapNoError;

}
