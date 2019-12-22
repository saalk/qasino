package cloud.qasino.card.service.impl;

import cloud.qasino.card.service.IPlayerService;
import cloud.qasino.card.service.common.AbstractService;
import cloud.qasino.card.dao.IPlayerDao;
import cloud.qasino.card.dao.common.IOperations;
import cloud.qasino.card.entity.Player;
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
