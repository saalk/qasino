package nl.knikit.card.dao.common;

import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
import java.util.List;

public interface IOperations<T extends Serializable> {

    T findOneWithString(final String id);

    T findOne(final int id);

    List<T> findAll(String column, String direction);

    List<T> findAllWhere(final String column, final String inputValue);
    
    <SK> List<T> findAllByAttributes(AttributesHashMap<T> attributes, SingularAttribute<T, SK> order);
    
    T create(final T entity);
    
    List<T> createAll(final List<T> entity);
    
    T createDefaultGame(final T entity);

    T update(final T entity);
    
    T updateStateInGame(final T entity);

    void deleteOne(final T entity);

    void deleteAll(final T entity);

    void deleteAllByIds(final T entity, final List<String> ids);
}
