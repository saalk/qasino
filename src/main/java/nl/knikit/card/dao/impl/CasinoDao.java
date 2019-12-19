package nl.knikit.card.dao.impl;

import nl.knikit.card.dao.ICasinoDao;
import nl.knikit.card.dao.common.AbstractHibernateDao;
import nl.knikit.card.entity.Casino;
import org.springframework.stereotype.Repository;

@Repository
public class CasinoDao extends AbstractHibernateDao<Casino> implements ICasinoDao {

    public CasinoDao() {
        super();

        setClazz(Casino.class);
    }

    // API

}
