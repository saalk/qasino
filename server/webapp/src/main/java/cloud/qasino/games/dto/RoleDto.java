package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.Position;
import cloud.qasino.games.database.security.Privilege;
import cloud.qasino.games.database.security.Visitor;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class RoleDto {

    // core
    private long roleId;
    private String name;

    // ref
//    private Collection<Visitor> visitors; // ignore

    private List<String> privilegeNames;

}

