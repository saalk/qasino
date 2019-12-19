package nl.knikit.card.dao.impl;

import nl.knikit.card.dao.IDeckDao;
import nl.knikit.card.dao.common.AbstractHibernateDao;
import nl.knikit.card.entity.Deck;
import org.springframework.stereotype.Repository;

@Repository
public class DeckDao extends AbstractHibernateDao<Deck> implements IDeckDao {

    public DeckDao() {
        super();

        setClazz(Deck.class);
    }

}
