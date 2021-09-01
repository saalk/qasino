package cloud.qasino.quiz.dto.enums;

import cloud.qasino.quiz.entity.enums.quiz.Face;
import cloud.qasino.quiz.entity.enums.quiz.Location;
import cloud.qasino.quiz.entity.enums.quiz.Position;
import lombok.Getter;

import java.util.Map;

@Getter
public class QuizEnums {

    Map<String, Location> location = Location.locationMapNoError;
    Map<String, Position> position = Position.positionMapNoError;
    Map<String, Face> face = Face.faceMapNoError;

}
