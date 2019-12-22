package cloud.qasino.card.dao.impl;

import cloud.qasino.card.dao.IDeckDao;
import cloud.qasino.card.dao.common.AbstractHibernateDao;
import cloud.qasino.card.entity.Deck;
import org.springframework.stereotype.Repository;

@Repository
public class DeckDao extends AbstractHibernateDao<Deck> implements IDeckDao {

    public DeckDao() {
        super();

        setClazz(Deck.class);
    }

}
