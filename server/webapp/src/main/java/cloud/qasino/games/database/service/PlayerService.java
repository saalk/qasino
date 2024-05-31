package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.security.Visitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository repository;
    @Autowired

    Player createHumanPlayerForVisitor(Visitor initiator, Game game, Avatar avatar) {
        List<Player> allPlayersForTheGame = repository.findByGame(game);
        return repository.save(new Player(
                initiator,
                game,
                Role.INITIATOR,
                initiator.getBalance(),
                allPlayersForTheGame.size()+1,
                avatar,
                AiLevel.HUMAN));
    }

    Player addBotPlayerToAGame(Visitor initiator, Game game, Avatar avatar) {
        List<Player> allPlayersForTheGame = repository.findByGame(game);
        return repository.save(new Player(
                initiator,
                game,
                Role.INITIATOR,
                initiator.getBalance(),
                allPlayersForTheGame.size()+1,
                avatar,
                AiLevel.HUMAN));
    }
}
