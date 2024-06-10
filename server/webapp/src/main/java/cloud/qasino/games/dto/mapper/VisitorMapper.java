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

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "using2FA", ignore = true)
    @Mapping(target = "aliasSequence", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "securedLoan", ignore = true)
    @Mapping(target = "year", ignore = true)
    @Mapping(target = "month", ignore = true)
    @Mapping(target = "week", ignore = true)
    @Mapping(target = "weekday", ignore = true)
    @Mapping(target = "players", ignore = true)
    @Mapping(target = "leagues", ignore = true)
    @Mapping(target = "roles", ignore = true)
    public abstract Visitor visitorDTOToVisitor(VisitorDTO visitor);


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
