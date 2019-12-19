package nl.knikit.card.service.impl;

import nl.knikit.card.dao.ICardDao;
import nl.knikit.card.dao.common.IOperations;
import nl.knikit.card.entity.Card;
import nl.knikit.card.service.ICardService;
import nl.knikit.card.service.common.AbstractService;
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
