package cloud.qasino.card.dao.impl;

import cloud.qasino.card.dao.IHandDao;
import cloud.qasino.card.entity.Hand;
import cloud.qasino.card.dao.common.AbstractHibernateDao;
import org.springframework.stereotype.Repository;

@Repository
public class HandDao extends AbstractHibernateDao<Hand> implements IHandDao {

    public HandDao() {
        super();

        setClazz(Hand.class);
    }

}
