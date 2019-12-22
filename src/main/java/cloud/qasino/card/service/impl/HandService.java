package cloud.qasino.card.service.impl;

import cloud.qasino.card.dao.IHandDao;
import cloud.qasino.card.dao.common.IOperations;
import cloud.qasino.card.entity.Hand;
import cloud.qasino.card.service.IHandService;
import cloud.qasino.card.service.common.AbstractService;
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
