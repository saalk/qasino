package nl.knikit.card.dao.impl;

import nl.knikit.card.dao.ICardDao;
import nl.knikit.card.dao.common.AbstractHibernateDao;
import nl.knikit.card.entity.Card;
import org.springframework.stereotype.Repository;

@Repository
public class CardDao extends AbstractHibernateDao<Card> implements ICardDao {

    public CardDao() {
        super();

        setClazz(Card.class);
    }

}
