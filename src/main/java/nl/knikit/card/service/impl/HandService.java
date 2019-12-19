package nl.knikit.card.service.impl;

import nl.knikit.card.dao.IHandDao;
import nl.knikit.card.dao.common.IOperations;
import nl.knikit.card.entity.Hand;
import nl.knikit.card.service.IHandService;
import nl.knikit.card.service.common.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HandService extends AbstractService<Hand> implements IHandService {

    @Autowired
    private IHandDao dao;

    public HandService() {
        super();
    }

    // API

    @Override
    protected IOperations<Hand> getDao() {
        return dao;
    }

}
