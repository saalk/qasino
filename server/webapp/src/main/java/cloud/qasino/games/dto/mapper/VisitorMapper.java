package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.VisitorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class VisitorMapper {

//    VisitorMapper INSTANCE = Mappers.getMapper(VisitorMapper.class);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "admin", source = "visitor", qualifiedByName = "isTheAdmin")
    @Mapping(target = "user", source = "visitor", qualifiedByName = "isTheUser")
    @Mapping(target = "repayPossible", source = "visitor", qualifiedByName = "canRepay")
    public abstract VisitorDTO visitorToVisitorDTO(Visitor visitor);

    @Named("canRepay")
    protected boolean canRepay(Visitor visitor){
        return visitor.getBalance() >= visitor.getSecuredLoan();
    }
    @Named("isTheAdmin")
    protected boolean isTheAdmin(Visitor visitor){
        return false;
    }
    @Named("isTheUser")
    protected boolean isTheUser(Visitor visitor){
        return true;
    }
}
