package cloud.qasino.card.service.impl;

import cloud.qasino.card.service.common.AbstractService;
import cloud.qasino.card.dao.ICardDao;
import cloud.qasino.card.dao.common.IOperations;
import cloud.qasino.card.entity.Card;
import cloud.qasino.card.service.ICardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService extends AbstractService<Card> implements ICardService {

    @Autowired
    private ICardDao dao;

    public CardService() {
        super();
    }

    // API

    @Override
    protected IOperations<Card> getDao() {
        return dao;
    }

}
