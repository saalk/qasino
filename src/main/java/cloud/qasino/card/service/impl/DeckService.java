package cloud.qasino.card.service.impl;

import cloud.qasino.card.service.IDeckService;
import cloud.qasino.card.service.common.AbstractService;
import cloud.qasino.card.dao.IDeckDao;
import cloud.qasino.card.dao.common.IOperations;
import cloud.qasino.card.entity.Deck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeckService extends AbstractService<Deck> implements IDeckService {

    @Autowired
    private IDeckDao dao;

    public DeckService() {
        super();
    }

    // API

    @Override
    protected IOperations<Deck> getDao() {
        return dao;
    }

}
