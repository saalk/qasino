package cloud.qasino.card.dto.enumrefs;

import cloud.qasino.card.entity.enums.card.Face;
import cloud.qasino.card.entity.enums.card.Location;
import cloud.qasino.card.entity.enums.card.Position;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
public class CardEnums {

    Map<String, Location> location = Location.locationMapNoError;
    Map<String, Position> position = Position.positionMapNoError;
    Map<String, Face> face = Face.faceMapNoError;

}
