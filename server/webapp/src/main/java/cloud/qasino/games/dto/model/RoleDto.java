package cloud.qasino.games.dto.model;

import lombok.Data;

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

