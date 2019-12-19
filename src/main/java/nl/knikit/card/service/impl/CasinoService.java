package nl.knikit.card.service.impl;

import nl.knikit.card.dao.ICasinoDao;
import nl.knikit.card.dao.common.IOperations;
import nl.knikit.card.entity.Casino;
import nl.knikit.card.service.ICasinoService;
import nl.knikit.card.service.common.AbstractService;
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
