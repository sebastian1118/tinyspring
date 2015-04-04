package org.triiskelion.tinyspring.security;

import java.util.HashMap;

/**
 * @author Sebastian MA
 */
public class AuthenticationResult {

	protected boolean success;

	protected TinyUser user;

	protected HashMap<String, Object> attributes = new HashMap<>(5);

	public static AuthenticationResult of(TinyUser user) {

		if(user == null) {
			return denied();
		}
		return new AuthenticationResult(user);
	}

	public static AuthenticationResult denied() {

		return new AuthenticationResult(null);
	}

	protected AuthenticationResult(TinyUser user) {

		this.user = user;
		this.success = user != null;
	}

	public TinyUser getUser() {

		return user;
	}

	public boolean isSuccess() {

		return success;
	}

	public Object setAttribute(String key, Object value) {

		return this.attributes.put(key, value);
	}

	public Object getAttribute(String key) {

		return this.attributes.get(key);
	}

}
