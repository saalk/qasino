package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.exception.MyBusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cloud.qasino.games.config.Constants.DEFAULT_PAWN_SHIP_BOT;

@Service
public class PlayerServiceOld {

    // @formatter:off
    @Autowired private PlayerRepository repository;

    // Since Java 8, there are several static methods added to the Comparator interface
    // that can take lambda expressions to create a Comparator object.
    // We can use its comparing() method to construct a Comparator

    // This Comparator can compare seats and can be passed to a compare function
    public static Comparator<Player> playerSeatComparator() {
        return Comparator.comparing(Player::getSeat);
    }

    public Player acceptInvitationForAGame(Player invitee) {
        invitee.setRole(Role.ACCEPTED);
        return repository.save(invitee);
    }
    public Player rejectInvitationForAGame(Player invitee) {
        invitee.setRole(Role.REJECTED);
        return repository.save(invitee);
    }
    public List<Player> seatOneUpForPlayer(Player seatUp) {
        List<Player> allPlayersForTheGame = repository.findByGame(seatUp.getGame());
        if (allPlayersForTheGame.size() == 1) return allPlayersForTheGame;
        if (allPlayersForTheGame.size() == 2) {
            // just swap
            allPlayersForTheGame.get(0).setSeat(2);
            allPlayersForTheGame.get(1).setSeat(1);
            return allPlayersForTheGame;
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
            return allPlayersForTheGame;
        }
        // swap one up
        allPlayersForTheGame.get(currentSeat - 1).setSeat(currentSeat + 1);
        allPlayersForTheGame.get(currentSeat).setSeat(currentSeat-1);
        return allPlayersForTheGame;
    }
    public Player addHumanVisitorPlayerToAGame(Visitor initiator, Game game, Avatar avatar) {
        List<Player> allPlayersForTheGame = repository.findByGame(game);
        String avatarName = "avatarName";
        Player visitor = new Player(
                initiator,
                game,
                Role.INITIATOR,
                initiator.getBalance(),
                allPlayersForTheGame.size()+1,
                avatar,
                avatarName,
                AiLevel.HUMAN);
        return repository.save(visitor);
    }
    public Player addInvitedHumanPlayerToAGame(Visitor invitee, Game game, Avatar avatar) {
        List<Player> allPlayersForTheGame = repository.findByGame(game);
        String avatarName = "avatarName";
        Player player = new Player(
            null,
            game,
            Role.INVITED,
            invitee.getBalance(),
            allPlayersForTheGame.size()+1,
            avatar,
            avatarName,
            AiLevel.HUMAN);
        return repository.save(player);
    }
    public Player addBotPlayerToAGame(Game game, Avatar avatar, AiLevel aiLevel) {
        if (aiLevel != null && aiLevel == AiLevel.HUMAN) {
            throw new MyBusinessException("addBotPlayerToAGame", "this aiLevel cannot become a bot player [" + aiLevel + "]");
        }
        List<Player> allPlayersForTheGame = repository.findByGame(game);
        int fiches = (int) (Math.random() * DEFAULT_PAWN_SHIP_BOT + 1);
        String avatarName = "avatarName";
        Player bot = new Player(
                null,
                game,
                Role.BOT,
                fiches,
                allPlayersForTheGame.size()+1,
                avatar,
                avatarName,
                aiLevel);
        return repository.save(bot);
    }
}
