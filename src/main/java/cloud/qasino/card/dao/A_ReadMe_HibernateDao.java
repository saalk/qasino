package cloud.qasino.card.dao;

public class A_ReadMe_HibernateDao {

/*

     http://www.baeldung.com/simplifying-the-data-access-layer-with-spring-and-java-generics
     This baeldung article discussed the simplification of the Data Access Layer by providing a
     single, reusable implementation of a generic DAO. This implementation was presented in both a
     Hibernate and a JPA based environment. The result is a streamlined persistence layer,
     with no unnecessary clutter.


     // common hibernate crud base layer using the safety of generics class<T> for model classes:
     // >dao.common
     interface IOperations<T extends Serialiable> { T findOne(int id);}
     abstract class AbstractHibernateDao<T extends Serializable> implements IOperations<T> {
     // all the hb session factory logic without spring jpatemplates - autowires the session factory
     // sesssionFactory.createQuery | persist | merge | delete
     }

     // specific jpa dao layer :
     // >dao
     interface IParentDao extends IOperation<Parent> {}
     // >dao.impl
     @Repository class ParentDao extends AbstractHibernateDao<Parent> implements IParentDao { setClazz in constr}


     // implementation of the generic dao layer for scoping and beans
     // >dao.common
     interface IGenericDao<T extends Serializable> extends IOperations<T> {}
     @Repository
     @Scope(BeanDefinition.SCOPE_PROTOTYPE) // use multiple dao's with different parameters / entities
     class GenericHibernateDao<T extends Serializable> extends AbstractHibernateDao<T> implements IGenericDao<T> {}

     // finally the service
     @Service class ParentService implements IParentService{
     IGenericDao< Foo > dao;
     @Autowired public void setDao( IGenericDao< Parent > daoToSet ){
     dao = daoToSet;
     dao.setClazz( Parent.class );
     }

     // Spring setter injection so customized with the xxx.class object ...

     }


     // ******************************************
     // without hibernate but with jpa is becomes:
     // >dao.common
     abstract class AbstractJpaDao< T extends Serializable > {
     // all the jpa entity manager logic without spring jpatemplates - @PersistenceContext the entitymanager
     // entityManager.createQuery | persist | merge | delete | remove
     }
     @Repository
     @Scope( BeanDefinition.SCOPE_PROTOTYPE )
     public class GenericJpaDao< T extends Serializable > extends AbstractJpaDao< T > implements IGenericDao< T > { // }
*/

}
