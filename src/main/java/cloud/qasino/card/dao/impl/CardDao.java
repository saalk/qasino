package cloud.qasino.card.dao.impl;

import cloud.qasino.card.dao.ICardDao;
import cloud.qasino.card.entity.Card;
import cloud.qasino.card.dao.common.AbstractHibernateDao;
import org.springframework.stereotype.Repository;

@Repository
public class CardDao extends AbstractHibernateDao<Card> implements ICardDao {

    public CardDao() {
        super();

        setClazz(Card.class);
    }

}
