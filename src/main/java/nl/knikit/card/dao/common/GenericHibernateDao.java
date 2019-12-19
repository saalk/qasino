package nl.knikit.card.dao.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class GenericHibernateDao<T extends Serializable> extends AbstractHibernateDao<T> implements IGenericDao<T> {
	
	//
}