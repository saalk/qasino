package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerState;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.PlayerDto;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.dto.mapper.GameMapper;
import cloud.qasino.games.dto.mapper.PlayerMapper;
import cloud.qasino.games.dto.mapper.VisitorMapper;
import cloud.qasino.games.exception.MyBusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cloud.qasino.games.config.Constants.DEFAULT_PAWN_SHIP_BOT;

@Service
public class PlayerService {

    // @formatter:off
    @Autowired private GameRepository gameRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private PlayerRepository playerRepository;
    PlayerMapper playerMapper;
    VisitorMapper visitorMapper;
    GameMapper gameMapper;

    // Since Java 8, there are several static methods added to the Comparator interface
    // that can take lambda expressions to create a Comparator object.
    // We can use its comparing() method to construct a Comparator

    // This Comparator can compare seats and can be passed to a compare function
    public static Comparator<Player> playerSeatComparator() {
        return Comparator.comparing(Player::getSeat);
    }

    public PlayerDto acceptInvitationForAGame(PlayerDto playerDto) {
        Player invitee = playerMapper.fromDto(playerDto);
        invitee.setPlayerState(PlayerState.ACCEPTED);
        Player accepted =  playerRepository.save(invitee);
        return playerMapper.toDto(accepted);
    }
    public PlayerDto rejectInvitationForAGame(PlayerDto playerDto) {
        Player invitee = playerMapper.fromDto(playerDto);
        invitee.setPlayerState(PlayerState.REJECTED);
        Player rejected =  playerRepository.save(invitee);
        return playerMapper.toDto(rejected);
    }
    public List<PlayerDto> seatOneUpForPlayer(PlayerDto playerDto) {
        Player seatUp = playerMapper.fromDto(playerDto);
        List<Player> allPlayersForTheGame = playerRepository.findByGame(seatUp.getGame());

        List<PlayerDto> playerDtos = allPlayersForTheGame.stream()
                .map(player -> playerMapper.toDto(player))
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
    public PlayerDto addHumanVisitorPlayerToAGame(VisitorDto visitorDto, GameDto gameDto, Avatar avatar) {
        Game game = gameMapper.gameDtoToGame(gameDto);
        Visitor initiator = visitorMapper.visitorDtoToVisitor(visitorDto);

        List<Player> allPlayersForTheGame = playerRepository.findByGame(game);
        String avatarName = "avatarName";
        Player visitor = new Player(
                initiator,
                game,
                PlayerState.INITIATOR,
                initiator.getBalance(),
                allPlayersForTheGame.size()+1,
                avatar,
                avatarName,
                AiLevel.HUMAN);
        Player newPlayer =  playerRepository.save(visitor);
        return playerMapper.toDto(newPlayer);
    }
    public PlayerDto addInvitedHumanPlayerToAGame(VisitorDto visitorDto, GameDto gameDto, Avatar avatar) {
        Game game = gameMapper.gameDtoToGame(gameDto);
        Visitor invitee = visitorMapper.visitorDtoToVisitor(visitorDto);

        List<Player> allPlayersForTheGame = playerRepository.findByGame(game);
        String avatarName = "avatarName";
        Player player = new Player(
            null,
            game,
            PlayerState.INVITED,
            invitee.getBalance(),
            allPlayersForTheGame.size()+1,
            avatar,
            avatarName,
            AiLevel.HUMAN);
        Player newPlayer =  playerRepository.save(player);
        return playerMapper.toDto(newPlayer);
    }
    public PlayerDto addBotPlayerToAGame(GameDto gameDto, Avatar avatar, AiLevel aiLevel) {
        Game game = gameMapper.gameDtoToGame(gameDto);

        if (aiLevel != null && aiLevel == AiLevel.HUMAN) {
            throw new MyBusinessException("addBotPlayerToAGame", "this aiLevel cannot become a bot player [" + aiLevel + "]");
        }
        List<Player> allPlayersForTheGame = playerRepository.findByGame(game);
        int fiches = (int) (Math.random() * DEFAULT_PAWN_SHIP_BOT + 1);
        String avatarName = "avatarName";
        Player bot = new Player(
                null,
                game,
                PlayerState.BOT,
                fiches,
                allPlayersForTheGame.size()+1,
                avatar,
                avatarName,
                aiLevel);
        Player newBot =  playerRepository.save(bot);
        return playerMapper.toDto(newBot);
    }
}
