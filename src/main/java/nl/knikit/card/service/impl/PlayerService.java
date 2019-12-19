package nl.knikit.card.service.impl;

import nl.knikit.card.dao.IPlayerDao;
import nl.knikit.card.dao.common.IOperations;
import nl.knikit.card.entity.Player;
import nl.knikit.card.service.IPlayerService;
import nl.knikit.card.service.common.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService extends AbstractService<Player> implements IPlayerService {

    @Autowired
    private IPlayerDao dao;

    public PlayerService() {
        super();
    }

    // API

    @Override
    protected IOperations<Player> getDao() {
        return dao;
    }

}
