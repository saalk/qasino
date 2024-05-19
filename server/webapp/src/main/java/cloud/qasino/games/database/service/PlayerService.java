package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository repository;

    //@Query("SELECT count(p) FROM playerS p WHERE p.game_id = ?1")
    int countByGame(Game game) {
        return repository.countByGame(game);
    };
}
