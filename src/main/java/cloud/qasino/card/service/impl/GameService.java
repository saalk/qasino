package cloud.qasino.card.service.impl;

import cloud.qasino.card.service.IGameService;
import cloud.qasino.card.service.common.AbstractService;
import cloud.qasino.card.dao.IGameDao;
import cloud.qasino.card.dao.common.IOperations;
import cloud.qasino.card.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService extends AbstractService<Game> implements IGameService {

    @Autowired
    private IGameDao dao;

    public GameService() {
        super();
    }

    // API

    @Override
    protected IOperations<Game> getDao() {
        return dao;
    }

}
