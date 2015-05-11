package org.triiskelion.tinyspring.security;

import java.util.HashMap;

/**
 * The authentication result. Extra information can be put in the attributes.
 *
 * @author Sebastian MA
 */
public class AuthenticationResult {

	protected boolean success;

	protected TinyUser user;

	protected HashMap<String, Object> attributes = new HashMap<>(5);

	protected AuthenticationResult(TinyUser user) {

		this.user = user;
		this.success = user != null;
	}

	/**
	 * Use this method to create a successful authentication result with
	 * a {@link TinyUser} instance.
	 *
	 * @param user
	 * 		the login user instance
	 *
	 * @return a successful authentication result
	 */
	public static AuthenticationResult of(TinyUser user) {

		if(user == null) {
			return denied();
		}
		return new AuthenticationResult(user);
	}

	/**
	 * Use this method to create a denied authentication result.
	 *
	 * @return a denied authentication result
	 */
	public static AuthenticationResult denied() {

		return new AuthenticationResult(null);
	}


	/**
	 * Get the login user.
	 *
	 * @return the login user or null if authentication failed.
	 */
	public TinyUser getUser() {

		return user;
	}

	/**
	 * @return <code>TRUE</code> if the authentication is successful or <code>FALSE</code>
	 * otherwise.
	 */
	public boolean isSuccess() {

		return success;
	}

	/**
	 * Set extra objects associated to this result.
	 *
	 * @param key
	 * @param value
	 *
	 * @return
	 */
	public Object setAttribute(String key, Object value) {

		return this.attributes.put(key, value);
	}

	/**
	 * Get extra objects associated to this result.
	 *
	 * @param key
	 *
	 * @return
	 */
	public Object getAttribute(String key) {

		return this.attributes.get(key);
	}

}
