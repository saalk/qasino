package nl.knikit.card.dao.impl;

import nl.knikit.card.dao.IGameDao;
import nl.knikit.card.dao.common.AbstractHibernateDao;
import nl.knikit.card.entity.Game;
import org.springframework.stereotype.Repository;

@Repository
public class GameDao extends AbstractHibernateDao<Game> implements IGameDao {

    public GameDao() {
        super();

        setClazz(Game.class);
    }

    // API

}
