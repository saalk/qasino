package cloud.qasino.card.dao.impl;

import cloud.qasino.card.entity.Casino;
import cloud.qasino.card.dao.ICasinoDao;
import cloud.qasino.card.dao.common.AbstractHibernateDao;
import org.springframework.stereotype.Repository;

@Repository
public class CasinoDao extends AbstractHibernateDao<Casino> implements ICasinoDao {

    public CasinoDao() {
        super();

        setClazz(Casino.class);
    }

    // API

}
