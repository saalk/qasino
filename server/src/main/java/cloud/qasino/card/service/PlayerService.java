package cloud.qasino.card.service;

import cloud.qasino.card.repositories.PlayerRepository;
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