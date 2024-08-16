package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.dto.model.GameDto;
import cloud.qasino.games.dto.model.LeagueShortDto;
import cloud.qasino.games.dto.model.PlayerDto;
import cloud.qasino.games.dto.model.ResultDto;
import cloud.qasino.games.dto.model.VisitorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ResultMapper {

    // for testing and use in other mappers
    ResultMapper INSTANCE = Mappers.getMapper(ResultMapper.class);

//    @Mapping(target = "players", source = "result", qualifiedByName = "players")
//    @Mapping(target = "visitor", source = "result", qualifiedByName = "visitor")
//    @Mapping(target = "game", source = "result", qualifiedByName = "game")

    @Mapping(target = "seatId", source = "result", qualifiedByName = "seatId")
    @Mapping(target = "human", source = "result", qualifiedByName = "human")
    @Mapping(target = "playerType", source = "result", qualifiedByName = "playerType")
    @Mapping(target = "fiches", source = "result", qualifiedByName = "fiches")
    @Mapping(target = "avatar", source = "result", qualifiedByName = "avatar")
    @Mapping(target = "avatarName", source = "result", qualifiedByName = "avatarName")
    @Mapping(target = "aiLevel", source = "result", qualifiedByName = "aiLevel")
    @Mapping(target = "winner", source = "result", qualifiedByName = "winner")
    @Mapping(target = "username", source = "result", qualifiedByName = "username")
    @Mapping(target = "alias", source = "result", qualifiedByName = "alias")
    @Mapping(target = "balance", source = "result", qualifiedByName = "balance")
    @Mapping(target = "securedLoan", source = "result", qualifiedByName = "securedLoan")
    @Mapping(target = "gameId", source = "result", qualifiedByName = "gameId")
    @Mapping(target = "ante", source = "result", qualifiedByName = "ante")
    @Mapping(target = "league", source = "result", qualifiedByName = "league")
    ResultDto toDto(Result result);
    List<ResultDto> toDtoList(List<Result> results);

    @Mapping(target = "visitor", ignore = true)
    @Mapping(target = "player", ignore = true)
    @Mapping(target = "game", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "year", ignore = true)
    @Mapping(target = "month", ignore = true)
    @Mapping(target = "week", ignore = true)
    @Mapping(target = "weekday", ignore = true)
    Result fromDto(ResultDto result);

    @Named("players")
    default PlayerDto players(Result result) {
        return PlayerMapper.INSTANCE.toDto(result.getPlayer(), result.getGame().getCards());
    }
    @Named("visitor")
    default VisitorDto visitor(Result result) {
        if (result.getPlayer().getPlayerId() != result.getVisitor().getVisitorId()) return null;
        return VisitorMapper.INSTANCE.toDto(result.getVisitor());
    }
    @Named("game")
    default GameDto game(Result result) {
        return GameMapper.INSTANCE.toDto(result.getGame(), result.getGame().getCards());
    }

    @Named("seatId")
    default int seatId(Result result) {
        return result.getPlayer().getSeat();
    }
    @Named("human")
    default boolean human(Result result) {
        return result.getPlayer().isHuman();
    }
    @Named("playerType")
    default PlayerType playerType(Result result) {
        return result.getPlayer().getPlayerType();
    }
    @Named("fiches")
    default int fiches(Result result) {
        return result.getPlayer().getFiches();
    }
    @Named("avatar")
    default Avatar avatar(Result result) {
        return result.getPlayer().getAvatar();
    }
    @Named("avatarName")
    default String avatarName(Result result) {
        return result.getPlayer().getAvatarName();
    }
    @Named("aiLevel")
    default AiLevel aiLevel(Result result) {
        return result.getPlayer().getAiLevel();
    }
    @Named("winner")
    default boolean winner(Result result) {
        return result.getPlayer().isWinner();
    }
    @Named("username")
    default String username(Result result) {
        if (result.getVisitor() == null)  return "";
        return result.getVisitor().getUsername();
    }
    @Named("alias")
    default String alias(Result result) {
        if (result.getVisitor() == null)  return "";
        return result.getVisitor().getAlias();
    }
    @Named("balance")
    default int balance(Result result) {
        if (result.getVisitor() == null)  return 0;
        return result.getVisitor().getBalance();
    }
    @Named("securedLoan")
    default int securedLoan(Result result) {
        if (result.getVisitor() == null)  return 0;
        return result.getVisitor().getSecuredLoan();
    }
    @Named("gameId")
    default long gameId(Result result) {
        return result.getGame().getGameId();
    }
    @Named("ante")
    default int ante(Result result) {
        return result.getGame().getAnte();
    }
    @Named("league")
    default LeagueShortDto league(Result result) {
        if(result.getGame().getLeague() == null) return null;
        return LeagueShortMapper.INSTANCE.toDto(result.getGame().getLeague());
    }

}
