package org.triiskelion.tinyspring.test.privilege;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.triiskelion.tinyspring.security.Privilege;

/**
 * @author Sebastian MA
 */
public class TestPrivilege {

	private static final Logger log = LoggerFactory.getLogger(TestPrivilege.class);

	Privilege a;

	Privilege b;

	@Before
	public void before() {

		a = new Privilege();
		a.getItems().put("admin", new Privilege("", "", 1));

		b = new Privilege();
		b.getItems().put("user", new Privilege("", "", 1));
	}

	@Test
	public void test() {

		Privilege c = a.merge(b);
		Assert.assertNotNull(c);


		System.out.println(c.getValue("admin"));
	}


}
