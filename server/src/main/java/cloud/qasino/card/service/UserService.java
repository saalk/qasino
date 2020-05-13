package cloud.qasino.card.service;

import cloud.qasino.card.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public long countUserName(String userName) {
        return repository.countByUserName(userName);
    }

}