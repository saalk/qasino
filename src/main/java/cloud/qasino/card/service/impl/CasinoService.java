package cloud.qasino.card.service.impl;

import cloud.qasino.card.service.ICasinoService;
import cloud.qasino.card.service.common.AbstractService;
import cloud.qasino.card.dao.ICasinoDao;
import cloud.qasino.card.dao.common.IOperations;
import cloud.qasino.card.entity.Casino;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CasinoService extends AbstractService<Casino> implements ICasinoService {

    @Autowired
    private ICasinoDao dao;

    public CasinoService() {
        super();
    }

    // API

    @Override
    protected IOperations<Casino> getDao() {
        return dao;
    }

}
