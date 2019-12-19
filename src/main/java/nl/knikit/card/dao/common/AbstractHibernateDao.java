package nl.knikit.card.dao.common;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import nl.knikit.card.entity.Game;
import nl.knikit.card.entity.enums.GameType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;

@Slf4j
// @SuppressWarnings("unchecked")
public abstract class AbstractHibernateDao<T extends Serializable> implements IOperations<T> {
	private Class<T> clazz;
	
	@Autowired
	SessionFactory sessionFactory;
	
	// API
	
	protected void setClazz(final Class<T> clazzToSet) {
		clazz = Preconditions.checkNotNull(clazzToSet);
	}
	
	@Override
	public T findOneWithString(final String id) {
		return getCurrentSession().get(clazz, id);
	}
	
	@Override
	public T findOne(final int id) {
		return getCurrentSession().get(clazz, id);
	}
	
	@Override
	public List<T> findAll(final String column, final String direction) {
		
		if (column.isEmpty() || direction.isEmpty()) { // changed from null to empty
			return getCurrentSession().createQuery("from " + clazz.getName()).list();
		} else {
			return getCurrentSession().createQuery("from " + clazz.getName() + " order by " + column + " " + direction).list();
		}
	}
	
	
	@Override
	public List<T> findAllWhere(final String column, final String inputValue) {
		
		String idMessage = String.format("findAllWhere dao entity: %s", clazz.getName());
		log.info(idMessage);
		
		// JPA Criteria API Query builder logic:
		// - builder=queryBuilder,
		// - criteria=query,
		// - c=class
		// - p=parameter
		// - gt, ge, lt, le, eq, like, ilike (case insensitive like) for operand
		
		// clazz.getClass() gets the runtime type of clazz
		// class.getName() gets the package + name of the class
		
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(clazz);
		try {
			
			//Root<T> rt = cq.from(clazz.getName().getClass());
			Root<T> root = criteria.from(clazz);
			
			// SELECT * FROM clazz
			criteria.select(root);
			
			// WHERE column = attribute
			switch (inputValue) {
				case "HIGHLOW":
					criteria.where(builder.equal(root.get(column), GameType.HIGHLOW));
					idMessage = String.format("findAllWhere dao inputValue is ENUM: %s", inputValue);
					log.info(idMessage);
					
					break;
				case "BLACKJACK":
					criteria.where(builder.equal(root.get(column), GameType.BLACKJACK));
					idMessage = String.format("findAllWhere dao inputValue is ENUM: %s", inputValue);
					log.info(idMessage);
					
					break;
				case "true":
					criteria.where(builder.equal(root.get(column), Boolean.TRUE));
					idMessage = String.format("findAllWhere dao inputValue is true boolean: %s", inputValue);
					log.info(idMessage);
					
					break;
				case "false":
					criteria.where(builder.equal(root.get(column), Boolean.FALSE));
					idMessage = String.format("findAllWhere dao inputValue is false boolean: %s", inputValue);
					log.info(idMessage);
					
					break;
				default:
					try {
						int num = Integer.parseInt(inputValue);
						
						// is an integer!
						criteria.where(builder.equal(root.get(column), num));
						idMessage = String.format("findAllWhere dao inputValue is integer: %d", num);
						log.info(idMessage);
						
					} catch (NumberFormatException e) {
						
						// not an integer!
						criteria.where(builder.equal(root.get(column), inputValue));
						idMessage = String.format("findAllWhere dao inputValue is string: %s", inputValue);
						log.info(idMessage);
						
					}
			}
			log.info(idMessage);
			
			return getCurrentSession().createQuery(criteria).list();
			
		} catch (Exception e) {
			String errorMessage = String.format("ErrorResponse findAllWhere, class: %s column: %s where: %s query: %s error: %s", clazz.getName(), column, inputValue, criteria, e);
			log.error(errorMessage);
			throw e;
		}
	}
	
	@Override
	/**
	 * Find multiple records by multiple attributes. Searches using AND.
	 *
	 * @param <SK>
	 *            attribute
	 * @param attributes
	 *            AttributeHashMap of SingularAttributes and values
	 * @param order SingularAttribute to order by:
	 *              You may pass in an array of attributes and a order by clause will be
	 *              added for each attribute in turn e.g. order by order[0], order[1] ....
	 * @return a list of matching entities
	 */
	public <SK> List<T> findAllByAttributes(AttributesHashMap<T> attributes, SingularAttribute<T, SK> order)
	{
		
		CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(clazz);
		Root<T> root = criteria.from(clazz);
		criteria.select(root);
		
		Predicate where = builder.conjunction();
		for (Entry<SingularAttribute<T, Object>, Object> attr : attributes.entrySet())
		{
			where = builder.and(where, builder.equal(root.get(attr.getKey()), attr.getValue()));
		}
		criteria.where(where);
		
		if (order != null)
		{
			criteria.orderBy(builder.asc(root.get(order)));
		}
		List<T> results = getCurrentSession().createQuery(criteria).getResultList();
		
		return results;
	}
	
	@Override
	public T create(final T entity) {
		
		String message = String.format("Entity to create in DAO: %s", entity.toString());
		log.info(message);
		
		Preconditions.checkNotNull(entity);
		try {
			// getCurrentSession().persist(entity);
			getCurrentSession().save(clazz.getName(), entity);
			//getCurrentSession().flush();
			
		} catch (Exception e) {
			String errorMessage = String.format("Entity create error: %s in DAO by entity: %s", e, entity);
			log.error(errorMessage);
			throw e;
		}
		return entity;
	}
	
	
	@Override
	public List<T> createAll(final List<T> entities) {
		
		String message = String.format("entities to create in DAO: %s", entities.toString());
		log.info(message);
		
		Preconditions.checkNotNull(entities);
		try {
			// getCurrentSession().persist(entity);
			for (T entity: entities) {
				getCurrentSession().save(clazz.getName(), entity);
				//getCurrentSession().flush();
			}
		} catch (Exception e) {
			String errorMessage = String.format("Entity create error: %s in DAO by entity: %s", e, entities);
			log.error(errorMessage);
			throw e;
		}
		return entities;
	}
	
	@Override
	public T update(final T entity) {
		
		String message = String.format("Entity to update in DAO: %s", entity.toString());
		log.info(message);
		
		Preconditions.checkNotNull(entity);
		try {
			// getCurrentSession().merge(entity);
			getCurrentSession().update(clazz.getName(), entity);
		} catch (Exception e) {
			String errorMessage = String.format("Entity to update error: %s in DAO by entity: %s", e, entity);
			log.error(errorMessage);
			throw e;
		}
		return entity;
	}
	
	// the deletes
	@Override
	public void deleteOne(final T entity) {
		
		String message = String.format("Entity to delete in DAO: %s", entity.toString());
		log.info(message);
		Preconditions.checkNotNull(entity);
		try {
			getCurrentSession().delete(entity);
		} catch (Exception e) {
			String errorMessage = String.format("Entity to delete error: %s in DAO by entity: %s", e, entity);
			log.error(errorMessage);
			throw e;
		}
	}
	
	@Override
	//todo check boot alternatives for this @Modifying
	public void deleteAll(final T entity) {
		
		String message = String.format("Entity to delete all in DAO: %s", entity.toString());
		log.info(message);
		Preconditions.checkNotNull(entity);
		try {
			getCurrentSession().createQuery("DELETE FROM " + entity.toString()).executeUpdate();
		} catch (Exception e) {
			String errorMessage = String.format("Entity to delete all error: %s in DAO by entity: %s", e, entity);
			log.error(errorMessage);
			throw e;
		}
	}
	
	@Override
	public void deleteAllByIds(final T entity, final List<String> ids) {
		
		try {
			
			for (String id : ids) {
				
				String idMessage = String.format("Entity to delete in DAO by list of ids has id: %s", id);
				log.info(idMessage);
				T oneEntity = findOne(Integer.parseInt(id));
				
				String message = String.format("Entity to delete by id in DAO: %s", entity.toString());
				log.info(message);
				
				Preconditions.checkState(oneEntity != null);
				
				getCurrentSession().delete(oneEntity);
			}
		} catch (Exception e) {
			String errorMessage = String.format("Entity to delete error: %s in DAO by list of ids: %s", e, ids);
			log.error(errorMessage);
			throw e;
		}
	}
	
	@Override
	public T createDefaultGame(T entity) {
		
		String message = String.format("Entity default to create in DAO: %s", entity.toString());
		log.info(message);
		
		Preconditions.checkNotNull(entity);
		try {
			Game gameToCreate = (Game) entity;
			gameToCreate.setGameType(GameType.HIGHLOW);
			// the state and gametype must be is supplied
			getCurrentSession().save((T) gameToCreate);
			//getCurrentSession().flush();
		} catch (Exception e) {
			String errorMessage = String.format("Entity default to create error: %s in DAO by entity: %s", e, entity);
			log.error(errorMessage);
			throw e;
		}
		return entity;
	}
	
	@Override
	public T updateStateInGame(T entity) {
		
		Preconditions.checkNotNull(entity);
		Game stateToUpdate;
		try {
			Game gameToUpdate = (Game) entity;
			stateToUpdate = (Game) findOne(gameToUpdate.getGameId());
			stateToUpdate.setState(gameToUpdate.getState());
			String message = String.format("Entity state to update in DAO: %s - the state is: %s", entity.toString(),gameToUpdate.getState());
			log.info(message);
			getCurrentSession().update(String.valueOf(Game.class), stateToUpdate);
			//getCurrentSession().flush();
			
		} catch (Exception e) {
			String errorMessage = String.format("Entity state to update error: %s in DAO by entity: %s", e, entity);
			log.error(errorMessage);
			throw e;
		}
		return (T) stateToUpdate;
	}
	
	// private method
	protected Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public String toString() {
		return "AbstractHibernateDao{" +
				       "clazz=" + clazz +
				       '}';
	}
	
}