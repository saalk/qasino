package nl.knikit.card.dao.impl;

import nl.knikit.card.dao.IHandDao;
import nl.knikit.card.dao.common.AbstractHibernateDao;
import nl.knikit.card.entity.Hand;
import org.springframework.stereotype.Repository;

@Repository
public class HandDao extends AbstractHibernateDao<Hand> implements IHandDao {

    public HandDao() {
        super();

        setClazz(Hand.class);
    }

}
