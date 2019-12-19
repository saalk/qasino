package nl.knikit.card.service.impl;

import nl.knikit.card.dao.IDeckDao;
import nl.knikit.card.dao.common.IOperations;
import nl.knikit.card.entity.Deck;
import nl.knikit.card.service.IDeckService;
import nl.knikit.card.service.common.AbstractService;
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
