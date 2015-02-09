package org.triiskelion.tinyspring.dao.test;

import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.triiskelion.tinyspring.dao.OrderType;
import org.triiskelion.tinyspring.dao.TinyQuery;
import org.triiskelion.tinyspring.viewmodel.Page;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.triiskelion.tinyspring.dao.TinyPredicate.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian MA
 * Date: August 21, 2014
 * Time: 16:22
 */
public class TestTinyQuery {

	Logger log = Logger.getLogger(TestTinyQuery.class);

	EntityManager entityManager;

	String[] names = new String[]{ "alice", "beatrice", "carol", "daisy", "ellen", "ellen" };

	@Before
	public void before() {

		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("jpa-hsqldb");
		entityManager = emFactory.createEntityManager();
		entityManager.getTransaction().begin();

		List<User> users = new ArrayList<>();
		for(int i = 0; i < names.length; i++) {
			User entity = new User();
			entity.setName(names[i]);
			entity.setSort(i);

			entityManager.persist(entity);
			users.add(entity);
		}

		//		Book book = new Book();
		//		book.setTitle("Childhood's End");
		//		book.setUser(users.get(0));
		//		entityManager.persist(book);

		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIgnoreNull() {

		TinyQuery<User> query;
		query = new TinyQuery<>(entityManager, User.class, true);

		// where clause will be ignored thus query is as same as count all.
		long result = query.ignoreNull(true).select()
		                   .where(equal("name", null))
		                   .count();
		assertEquals(names.length, result);

		// this should throw an IllegalArgumentException
		query = new TinyQuery<>(entityManager, User.class, true);
		result = query.ignoreNull(false).select()
		              .where(equal("name", null))
		              .count();
	}

	@Test
	public void testDelete() {

		TinyQuery<User> query;
		query = new TinyQuery<>(entityManager, User.class, true);

		entityManager.getTransaction().begin();
		long result = query.delete()
		                   .where(equal("name", "alice"))
		                   .execute();
		entityManager.getTransaction().commit();
		assertEquals(1, result);

		query = new TinyQuery<>(entityManager, User.class, true);
		assertTrue(query.ignoreNull(false).select()
		                .where(equal("name", "alice"))
		                .hasNoResult());
	}

	@Test
	public void testSelect() {

		TinyQuery<User> query;
		query = new TinyQuery<>(entityManager, User.class, true);

		List<Object[]> result = query.select("name, sort")
		                             .orderBy("name", OrderType.ASC)
		                             .getUntypedResultList();

		assertEquals(names.length, result.size());
		assertEquals(2, result.get(0).length);
		assertEquals("alice", result.get(0)[0]);
		assertEquals(0, result.get(0)[1]);
	}

	@Test
	public void testDistinct() {

		/*TinyQuery<DummyEntity> query;
		query = new TinyQuery<>(entityManager, DummyEntity.class, true);

		List result = query.select().distinct()
		                   .where(equal("name", "ellen"))
		                   .getResultList();
		Assert.assertEquals(1, result.size());

		query = new TinyQuery<>(entityManager, DummyEntity.class, true);
		result = query.select()
		              .where(equal("name", "ellen"))
		              .getResultList();

		Assert.assertEquals(2, result.size());*/
	}

	@Test
	public void testCount() {

		TinyQuery<User> query;
		query = new TinyQuery<>(entityManager, User.class, true);

		long result = query.select().count();
		assertEquals(names.length, result);
	}

	@Test
	public void testFrom() {
		//todo
	}

	@Test
	public void testJoin() {
		//todo
	}

	@Test
	public void testWhereAndOr() {

		TinyQuery<User> query;
		query = new TinyQuery<>(entityManager, User.class, true);
		long result = query.select()
		                   .where(and(equal("name", "alice"), equal("sort", 0)))
		                   .or(and(equal("name", "carol"), equal("sort", 2)))
		                   .count();
		assertEquals(2, result);

		query = new TinyQuery<>(entityManager, User.class, true);
		result = query.select()
		              .where(or(equal("name", "alice"), equal("name", "beatrice")))
		              .and(or(equal("sort", 0), equal("sort", 1), equal("sort", 2)))
		              .count();
		assertEquals(2, result);
	}

	@Test
	public void testOrderBy() {

		TinyQuery<User> query;
		query = new TinyQuery<>(entityManager, User.class, true);
		List<User> result = query.select()
		                         .orderBy("name", OrderType.DESC)
		                         .orderBy("sort", OrderType.ASC)
		                         .getResultList();
		assertEquals(new Integer(4), result.get(0).getSort());
		assertEquals(new Integer(5), result.get(1).getSort());
	}

	@Test
	public void testGetPagedResult() {

		TinyQuery<User> query = new TinyQuery<>(entityManager, User.class, true);

		Page<User> result = query.select().page(2, 2).getPagedResult();
		assertEquals(names.length, result.getTotal());
		assertEquals(names.length / 2 + (names.length % 2 == 0 ? 0 : 1), result.getTotalPage());
		assertEquals(2, result.getPage());
		assertEquals(2, result.getMax());
		assertEquals(result.getDataSize(), result.getData().size());
	}

	@Test
	public void testPredicateIn() {

		TinyQuery<User> query = new TinyQuery<>(entityManager, User.class, true);

		List<User> result
				= query.select()
				       .where(in("name", listOf("alice", "beatrice", "daisy")))
				       .orderBy("name", OrderType.ASC)
				       .getResultList();

		assertEquals(3, result.size());
		assertEquals("alice", result.get(0).getName());
		assertEquals("beatrice", result.get(1).getName());
		assertEquals("daisy", result.get(2).getName());
	}


	@Test
	public void testQuery() {

		TinyQuery<User> query = new TinyQuery<>(entityManager, User.class, true);

		List<User> result
				= query.query("SELECT m FROM User m WHERE m.name=:name OR m.name=?1")
				       .param(1, "alice")
				       .param("name", "beatrice")
				       .getResultList();

		assertEquals(2, result.size());
		assertEquals("alice", result.get(0).getName());
		assertEquals("beatrice", result.get(1).getName());


		// raw query with pagination
		query = new TinyQuery<>(entityManager, User.class, true);
		Page<User> page
				= query.query("Select E FROM User E WHERE E.name<>:name")
				       .param("name", "ellen")
				       .page(2, 3)
				       .getPagedResult();

		assertEquals(1, page.getDataSize());
		assertEquals("daisy", page.getData().get(0).getName());
		assertEquals(2, page.getTotalPage());
		assertEquals(4, page.getTotal());


	}

	@Test
	public void testLimit() {

		TinyQuery<User> query = new TinyQuery<>(entityManager, User.class, true);

		List<User> result
				= query.query("SELECT m FROM User m")
				       .limit(1, 3)
				       .getResultList();

		assertEquals(3, result.size());
		assertEquals("beatrice", result.get(0).getName());
	}

	@Test
	public void testPage() {

		TinyQuery<User> query = new TinyQuery<>(entityManager, User.class, true);

		List<User> result
				= query.query("SELECT m FROM User m")
				       .page(2, 2)
				       .getResultList();

		assertEquals(2, result.size());
		assertEquals("carol", result.get(0).getName());
	}
}
