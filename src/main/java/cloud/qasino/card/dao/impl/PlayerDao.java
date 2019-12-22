package cloud.qasino.card.dao.impl;

import cloud.qasino.card.entity.Player;
import cloud.qasino.card.dao.IPlayerDao;
import cloud.qasino.card.dao.common.AbstractHibernateDao;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerDao extends AbstractHibernateDao<Player> implements IPlayerDao {

    public PlayerDao() {
        super();

        setClazz(Player.class);
    }

}
