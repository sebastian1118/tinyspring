package org.triiskelion.tinyspring.security;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Sebastian MA
 */
public abstract class TinySecurityManager {

	public static final String SESSION_NAME_USER = "TINY_USER_OBJECT";

	/**
	 * Try to authenticate using the given login and password.
	 * <p> If the authentication succeeded,
	 * an <code>TinyUser</code> instance is added to the session to mark current session as
	 * logged in.</p>
	 *
	 * @param session
	 * 		the http session
	 * @param login
	 * 		user's login
	 * @param password
	 * 		user's password
	 *
	 * @return {@link AuthenticationResult}
	 */
	public AuthenticationResult authenticate(HttpSession session, String login, String
			password) {

		AuthenticationResult result = this.doAuthenticate(session, login, password);
		if(result.success) {
			session.setAttribute(SESSION_NAME_USER, result.getUser());
		} else {
			session.setAttribute(SESSION_NAME_USER, null);
		}
		return result;
	}

	protected abstract AuthenticationResult doAuthenticate(HttpSession session,
	                                                       String login,
	                                                       String password);

	/**
	 * This method will be invoked to authenticate user if {@link SecurityCheck} is
	 * marked <code>stateless</code>
	 *
	 * @param request
	 * 		the http request
	 * @param response
	 * 		the http response
	 *
	 * @return
	 */
	protected abstract boolean doAuthenticateStatelessly(HttpServletRequest request,
	                                                     HttpServletResponse response);


	protected abstract void onNotLogin(HttpServletRequest request,
	                                   HttpServletResponse response);

	protected abstract void onRequireAllPrivilegesFail(HttpServletRequest request,
	                                                   HttpServletResponse response,
	                                                   TinyUser user, String[] requireRoles);

	protected abstract void onRequireAnyPrivilegeFail(HttpServletRequest request,
	                                                  HttpServletResponse response,
	                                                  TinyUser user, String[] requireRoles);

	public abstract void onRequireRolesFail(HttpServletRequest request,
	                                        HttpServletResponse response,
	                                        TinyUser user, String[] requireRoles);

}
