package cloud.qasino.games.database.service;

import cloud.qasino.games.database.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisitorService {

    @Autowired
    private VisitorRepository repository;

    public long countVisitorName(String visitorName) {
        return repository.countByVisitorName(visitorName);
    }

}