package nl.knikit.card.service.impl;

import nl.knikit.card.dao.IGameDao;
import nl.knikit.card.dao.common.IOperations;
import nl.knikit.card.entity.Game;
import nl.knikit.card.service.IGameService;
import nl.knikit.card.service.common.AbstractService;
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
