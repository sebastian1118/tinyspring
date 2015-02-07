tinyspring
==========
###TinySpring is a set of tools for spring-mvc-JPA projects.

### Features:

* Generic DAO class with query builder to create queries more easily.

* Utilities to make your MVC code cleaner. (View models, object mapping, etc.)

* (Planned) A minimal security framework to do the authentication and authorization.

* (Planned) An online API documentation built from Spring mvc annotation.

[![Build Status](https://travis-ci.org/sebastian1118/tinyspring.svg?branch=master)](https://travis-ci.org/sebastian1118/tinyspring)

# Usage

## Generic DAO & Query Builder

Assuming you have an Entity called `User` and you wish a DAO class to access `User`'s data from database.
You can simply create a `UserDao` class which extends `AbstractDao<User>` and implements the abastract method `getEntityClass()` in which returns the entity class we'd like to access:

    @Repository
    @Transactional
    public class UserDao extends AbstractDao<User> {
    
      	@Override
      	protected Class<User> getEntityClass() {
    
    		return User.class;
    	}
    }
    
Now we have our first DAO. It has already some out of box methods inherited from AbstractDao like `persist() merge() remove()
findById() findAll() totalCount()` etc.

If you wish to write customized queries, you can use `beginQuery()` to begin a method chain to help you make a JPA query more easily. 
Here's some examples:

If you wish to query from User with username 'bob':

    import static org.triiskelion.tinyspring.dao.TinyPredicate.equal;
    
    User result = userDao.beginQuery().select()
                       .where(equal("username", "bob"))
                       .getFirstResult();

If you wish to authenticate user 'bob' with password '123456':

    boolean authSucess = userDao.beginQuery().select()
                              .where(equal("username","bob"))
                              .and(equal("password","123456"))
                              .hasResult();
                              
      boolean authSucess = userDao.beginQuery().select()
                              .where(and(equal("username", "bob"), equal("password", "123456")))
                              .hasResult();
                              
        boolean authSucess = userDao.beginQuery().select()
                              .where(equal("username", "bob").and(equal("password", "123456")))
                              .hasResult();
                              
All three syntax are valid.


If your query is very complex or you are JPQL expert and you wish to write your JPQL directly:

    List<User> resultList = userDao.begeinQuery()
                                   .query("SELECT m FROM User m WHERE m.signInTime BETWEEN :a AND :b")
                                   .param("a", new Date("2014-07-01"))
                                   .param("b", new Date("2014-07-31"))
                                   .getResultList();
                                   

More demonstrations are coming.
