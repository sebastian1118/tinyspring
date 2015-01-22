package org.triiskelion.tinyspring.security;


import org.triiskelion.tinyspring.AbstractSpringBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian MA
 * Date: August 11, 2014
 * Time: 14:00
 */
public abstract class TinyAuthenticator extends AbstractSpringBean {

	public static final String SESSION_NAME_USER = "TINY_USER_OBJECT";

	protected String logoutSuccessUrl;

	protected String logoutFailureUrl;

	protected String loginSuccessUrl;

	protected String loginFailureUrl;

	public abstract AuthenticationResult authenticate(HttpSession session,
	                                                  String username,
	                                                  String password);

	public abstract void authenticateStatelessly(HttpServletRequest request, HttpServletResponse
			response);


	public void setLogoutSuccessUrl(String logoutSuccessUrl) {

		this.logoutSuccessUrl = logoutSuccessUrl;
	}

	public String getLogoutFailureUrl() {

		return logoutFailureUrl;
	}

	public void setLogoutFailureUrl(String logoutFailureUrl) {

		this.logoutFailureUrl = logoutFailureUrl;
	}

	public String getLoginSuccessUrl() {

		return loginSuccessUrl;
	}

	public String getLogoutSuccessUrl() {

		return logoutSuccessUrl;
	}

	public void setLoginSuccessUrl(String loginSuccessUrl) {

		this.loginSuccessUrl = loginSuccessUrl;
	}

	public String getLoginFailureUrl() {

		return loginFailureUrl;
	}

	public void setLoginFailureUrl(String loginFailureUrl) {

		this.loginFailureUrl = loginFailureUrl;
	}

	public class AuthenticationResult {

		protected boolean success;

		protected String url;

		protected TinyUser user;

		public AuthenticationResult(TinyUser user, boolean success, String url) {

			this.success = success;
			this.url = url;
			this.user = user;
		}

		public TinyUser getUser() {

			return user;
		}

		public void setUser(TinyUser user) {

			this.user = user;
		}

		public boolean isSuccess() {

			return success;
		}

		public void setSuccess(boolean success) {

			this.success = success;
		}

		public String getUrl() {

			return url;
		}

		public void setUrl(String url) {

			this.url = url;
		}
	}

}
