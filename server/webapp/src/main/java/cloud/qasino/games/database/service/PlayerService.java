package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.model.GameDto;
import cloud.qasino.games.dto.model.PlayerDto;
import cloud.qasino.games.dto.model.VisitorDto;
import cloud.qasino.games.dto.mapper.GameMapper;
import cloud.qasino.games.dto.mapper.PlayerMapper;
import cloud.qasino.games.dto.mapper.VisitorMapper;
import cloud.qasino.games.exception.MyBusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cloud.qasino.games.config.Constants.DEFAULT_PAWN_SHIP_BOT;
import static cloud.qasino.games.pattern.comparator.ComparatorUtil.playerSeatComparator;

@Service
@Lazy
public class PlayerService {

    // @formatter:off
    @Autowired private PlayerRepository playerRepository;

    // lifecycle of a player - aim to pass params and creation dto's for consistency for all services
    public PlayerDto addHumanVisitorPlayerToAGame(VisitorDto visitorDto, GameDto gameDto, Avatar avatar) {
        Game game = GameMapper.INSTANCE.fromDto(gameDto);
        Visitor initiator = VisitorMapper.INSTANCE.fromDto(visitorDto);

        List<Player> allPlayersForTheGame = playerRepository.findByGame(game);
        String avatarName = "avatarName";
        Player visitor = new Player(
                initiator,
                game,
                PlayerType.INITIATOR,
                initiator.getBalance(),
                allPlayersForTheGame.size()+1,
                avatar,
                avatarName,
                AiLevel.HUMAN);
        Player newPlayer =  playerRepository.save(visitor);
        return PlayerMapper.INSTANCE.toDto(newPlayer, null);
    }
    public PlayerDto addInvitedHumanPlayerToAGame(VisitorDto visitorDto, GameDto gameDto, Avatar avatar) {
        Game game = GameMapper.INSTANCE.fromDto(gameDto);
        Visitor invitee = VisitorMapper.INSTANCE.fromDto(visitorDto);

        List<Player> allPlayersForTheGame = playerRepository.findByGame(game);
        String avatarName = "avatarName";
        Player player = new Player(
                null,
                game,
                PlayerType.INVITED,
                invitee.getBalance(),
                allPlayersForTheGame.size()+1,
                avatar,
                avatarName,
                AiLevel.HUMAN);
        Player newPlayer =  playerRepository.save(player);
        return PlayerMapper.INSTANCE.toDto(newPlayer, null);
    }
    public PlayerDto addBotPlayerToAGame(GameDto gameDto, Avatar avatar, AiLevel aiLevel) {
        Game game = GameMapper.INSTANCE.fromDto(gameDto);

        if (aiLevel == AiLevel.HUMAN) {
            throw new MyBusinessException("addBotPlayerToAGame", "this aiLevel cannot become a bot player [" + aiLevel + "]");
        }
        List<Player> allPlayersForTheGame = playerRepository.findByGame(game);
        int fiches = (int) (Math.random() * DEFAULT_PAWN_SHIP_BOT + 1);
        String avatarName = "avatarName";
        Player bot = new Player(
                null,
                game,
                PlayerType.BOT,
                fiches,
                allPlayersForTheGame.size()+1,
                avatar,
                avatarName,
                aiLevel);
        Player newBot =  playerRepository.save(bot);
        return PlayerMapper.INSTANCE.toDto(newBot, null);
    }
    public PlayerDto updatePlayerFiches(PlayerDto playerDto, int fiches ) {
        Player player = PlayerMapper.INSTANCE.fromDto(playerDto);
        player.setFiches(fiches);
        Player updated =  playerRepository.save(player);
        return PlayerMapper.INSTANCE.toDto(updated, null);
    }
    public PlayerDto acceptInvitationForAGame(PlayerDto playerDto) {

        Player invitee = PlayerMapper.INSTANCE.fromDto(playerDto);
        invitee.setPlayerType(PlayerType.INVITEE);
        Player accepted =  playerRepository.save(invitee);
        return PlayerMapper.INSTANCE.toDto(accepted, null);
    }
    public PlayerDto rejectInvitationForAGame(PlayerDto playerDto) {
        Player invitee = PlayerMapper.INSTANCE.fromDto(playerDto);
        invitee.setPlayerType(PlayerType.REJECTED);
        Player rejected =  playerRepository.save(invitee);
        return PlayerMapper.INSTANCE.toDto(rejected, null);
    }
    public List<PlayerDto> seatOneUpForPlayer(PlayerDto playerDto) {
        Player seatUp = PlayerMapper.INSTANCE.fromDto(playerDto);
        List<Player> allPlayersForTheGame = playerRepository.findByGame(seatUp.getGame());

        List<PlayerDto> playerDtos = allPlayersForTheGame.stream()
                .map(player -> PlayerMapper.INSTANCE.toDto(player, null))
                .toList();

        if (allPlayersForTheGame.size() == 1) return playerDtos;
        if (allPlayersForTheGame.size() == 2) {
            // just swap
            allPlayersForTheGame.get(0).setSeat(2);
            allPlayersForTheGame.get(1).setSeat(1);
            return playerDtos;
        }
        // sort Players based on seat using the Comparator
        allPlayersForTheGame.stream()
                .sorted(playerSeatComparator())
                .collect(Collectors.toList());
        int currentSeat = allPlayersForTheGame.indexOf(seatUp) + 1;
        if (currentSeat == allPlayersForTheGame.size()) {
            // just swap first and last
            allPlayersForTheGame.get(0).setSeat(allPlayersForTheGame.size());
            allPlayersForTheGame.get(allPlayersForTheGame.size()-1).setSeat(1);
            return playerDtos;
        }
        // swap one up
        allPlayersForTheGame.get(currentSeat - 1).setSeat(currentSeat + 1);
        allPlayersForTheGame.get(currentSeat).setSeat(currentSeat-1);
        return playerDtos;
    }
}
