package org.triiskelion.tinyspring.security;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Sebastian MA
 */
@Deprecated
public abstract class TinyAuthenticator {

	public static final String SESSION_NAME_USER = "TINY_USER_OBJECT";


	public abstract AuthenticationResult authenticate(HttpSession session,
	                                                  String username,
	                                                  String password);

	public abstract boolean authenticateStatelessly(HttpServletRequest request, HttpServletResponse
			response);


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
