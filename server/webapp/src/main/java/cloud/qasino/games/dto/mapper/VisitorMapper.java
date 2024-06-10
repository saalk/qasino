//package cloud.qasino.games.dto.mapper;
//
//import cloud.qasino.games.database.security.Visitor;
//import cloud.qasino.games.dto.VisitorDTO;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.Named;
//import org.mapstruct.factory.Mappers;
//
//@Mapper
//public abstract class VisitorMapper {
//
////    VisitorMapper INSTANCE = Mappers.getMapper(VisitorMapper.class);
//
//    @Mapping(target = "visitorDTO.isAdmin", ignore = true)
//    @Mapping(target = "visitorDTO.isUser", ignore = true)
////    @Mapping(target = "visitorDTO.isRepayPossible", source = "visitor", qualifiedByName = "canRepay")
//    public abstract VisitorDTO visitorToVisitorDTO(Visitor visitor);
//
//    @Named("canRepay")
//    protected boolean canRepay(Visitor visitor){
//        return visitor.getBalance() >= visitor.getSecuredLoan();
//    }
//    // usage     VisitorDTO visitorDTO = VisitorMapper.INSTANCE.visitorToVisitorDto( visitor );
//}
