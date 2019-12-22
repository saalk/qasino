package cloud.qasino.card.dao.impl;

import cloud.qasino.card.dao.IGameDao;
import cloud.qasino.card.dao.common.AbstractHibernateDao;
import cloud.qasino.card.entity.Game;
import org.springframework.stereotype.Repository;

@Repository
public class GameDao extends AbstractHibernateDao<Game> implements IGameDao {

    public GameDao() {
        super();

        setClazz(Game.class);
    }

    // API

}
