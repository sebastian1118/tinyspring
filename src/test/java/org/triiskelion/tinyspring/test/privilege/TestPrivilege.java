package org.triiskelion.tinyspring.test.privilege;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.triiskelion.tinyspring.security.Privileges;

/**
 * @author Sebastian MA
 */
public class TestPrivilege {

	private static final Logger log = LoggerFactory.getLogger(TestPrivilege.class);

	Privileges a;

	Privileges b;

	@Before
	public void before() {

		a = new Privileges("admin", "admin role");
		a.getItems().put("admin", new Privileges("admin", "desc", 1));

		b = new Privileges("user", "user role");
		b.getItems().put("user", new Privileges("daf", "", 1));
	}

	@Test
	public void test() {

		Privileges c = a.merge(b);
		Assert.assertNotNull(c);


		Assert.assertEquals(1, c.getValue("admin"));
		Assert.assertEquals(-1, c.getValue("ad.min"));


		System.out.println(c);
	}


}
