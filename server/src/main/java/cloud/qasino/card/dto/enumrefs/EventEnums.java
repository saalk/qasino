package cloud.qasino.card.dto.enumrefs;

import cloud.qasino.card.entity.enums.event.Action;
import lombok.Getter;

import java.util.Map;

@Getter
public class EventEnums {

    Map<String, Action> action = Action.actionMapNoError;

}
