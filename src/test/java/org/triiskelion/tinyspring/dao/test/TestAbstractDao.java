package org.triiskelion.tinyspring.dao.test;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.triiskelion.tinyspring.dao.test.base.Book;
import org.triiskelion.tinyspring.dao.test.base.Person;
import org.triiskelion.tinyspring.dao.test.base.PersonDao;
import org.triiskelion.tinyspring.dao.test.base.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Sebastian MA
 */
public class TestAbstractDao {

	Logger log = LoggerFactory.getLogger(TestTinyQuery.class);

	EntityManager entityManager;

	String[] names = new String[]{ "alice", "beatrice", "carol", "daisy", "ellen", "ellen" };

	private PersonDao personDao;

	@Before
	public void before() {

		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("jpa-hsqldb");
		entityManager = emFactory.createEntityManager();

		personDao = new PersonDao();
		personDao.setEntityManager(entityManager);

		entityManager.getTransaction().begin();

		for(String name : names) {
			Person entity = new Person();
			entity.setName(name);
			entityManager.persist(entity);
		}

		List<User> users = new ArrayList<>();
		for(int i = 0; i < names.length; i++) {
			User entity = new User();
			entity.setName(names[i]);
			entity.setSort(i);

			entityManager.persist(entity);
			users.add(entity);
		}

		Book book = new Book();
		entityManager.persist(book);
		book.setTitle("Childhood's End");
		book.setUser(users.get(0));
		users.get(0).getBooks().add(book);

		book = new Book();
		entityManager.persist(book);
		book.setTitle("Harry Porter");
		book.setUser(users.get(0));
		users.get(0).getBooks().add(book);


		entityManager.getTransaction().commit();
	}

	@Test
	public void test() {

		assertEquals(6, personDao.findAll().size());
		assertEquals(6, personDao.totalCount());
		assertEquals("alice", personDao.findById(1).get().getName());
		assertFalse(personDao.findById(7).isPresent());

	}


}
