package cloud.qasino.card.service;

import cloud.qasino.card.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public long countAlias(String alias) {
        return repository.countByAlias(alias);
    }

}