package nl.knikit.card.dao.impl;

import nl.knikit.card.dao.IPlayerDao;
import nl.knikit.card.dao.common.AbstractHibernateDao;
import nl.knikit.card.entity.Player;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerDao extends AbstractHibernateDao<Player> implements IPlayerDao {

    public PlayerDao() {
        super();

        setClazz(Player.class);
    }

}
