package cloud.qasino.games.database.service;

import cloud.qasino.games.database.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository repository;

    public int countByGameId(int gameId) {
        final long count = repository.count();
        return (int) count;
    }
}