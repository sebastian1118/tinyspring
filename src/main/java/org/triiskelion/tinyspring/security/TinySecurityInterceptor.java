package org.triiskelion.tinyspring.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Sebastian MA
 */
public class TinySecurityInterceptor extends HandlerInterceptorAdapter {

	private static final Logger log = LoggerFactory.getLogger(TinySecurityInterceptor.class);

	TinySecurityManager securityManager;

	public TinySecurityManager getSecurityManager() {

		return securityManager;
	}

	public void setSecurityManager(TinySecurityManager securityManager) {

		this.securityManager = securityManager;
	}

	@Override
	public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object
			handler) throws Exception {

		if(handler instanceof ResourceHttpRequestHandler) {
			return true;
		}

		if(handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;

			SecurityCheck classAnnotation =
					handlerMethod.getMethod()
					             .getDeclaringClass()
					             .getAnnotation(SecurityCheck.class);

			SecurityCheck methodAnnotation =
					handlerMethod
							.getMethodAnnotation(SecurityCheck.class);

			String url = request.getRequestURI().replace(request.getContextPath(), "");

			// check method annotation
			if(methodAnnotation != null) { // if method is annotated
				if(!methodAnnotation.value()) {
					log.debug("Access granted [{}]: @SecurityCheck disabled.", url);
					return true;
				}
				return checkAndRespond(url, request, response,
						methodAnnotation.requireRoles(),
						methodAnnotation.requireAnyPrivileges(),
						methodAnnotation.requireAllPrivileges(),
						methodAnnotation.stateless());
			}

			// check type annotation
			if(classAnnotation == null) {
				log.debug(" Access granted [{}]: @SecurityCheck not found", url);
				return true;
			}

			if(!classAnnotation.value()) {
				log.debug("Access granted [{}]: @SecurityCheck disabled.", url);
				return true;
			}

			String[] matches = classAnnotation.matches();
			String[] excludes = classAnnotation.excludes();

			boolean requireCheck = false;

			for(String pattern : matches) {
				if(pattern.contains("**")) {
					pattern = pattern.replace("**", ".*");
				} else {
					pattern = pattern.replace("*", "[^/]*");
				}
				if(url.matches(pattern)) {
					requireCheck = true;
					break;
				}
			}

			if(!requireCheck) {
				log.debug("Access granted [{}]: pattern not match", url);
				return true;

			} else {

				for(String excludePattern : excludes) {
					if(excludePattern.contains("**")) {
						excludePattern = excludePattern.replace("**", ".*");
					} else {
						excludePattern = excludePattern.replace("*", "[^/]*");
					}
					excludePattern = ".*" + excludePattern + ".*";
					if(url.matches(excludePattern)) {
						requireCheck = false;
						log.debug("Access granted [{}]: excluded", url);
						break;
					}
				}
			}

			if(requireCheck) { // now check it
				return checkAndRespond(url, request, response,
						classAnnotation.requireRoles(),
						classAnnotation.requireAnyPrivileges(),
						classAnnotation.requireAllPrivileges(),
						classAnnotation.stateless());
			}

		}

		return true; //if fall here, access will be granted
	}

	private final boolean checkAndRespond(String url,
	                                      HttpServletRequest request,
	                                      HttpServletResponse response,
	                                      String[] requireRoles,
	                                      String[] requireAnyPrivileges,
	                                      String[] requireAllPrivileges,
	                                      boolean stateless)
			throws ServletException, IOException {

		log.debug("Security check for [{}], requireAnyPrivileges[{}], requireAllPrivileges[{}]",
				url,
				StringUtils.join(requireAnyPrivileges, ","),
				StringUtils.join(requireAllPrivileges, ",")
		         );


		if(stateless) { // doAuthenticate via request
			return securityManager.doAuthenticateStatelessly(request, response);
		}

		TinyUser user = (TinyUser) request.getSession()
		                                  .getAttribute(TinySecurityManager.SESSION_NAME_USER);

		if(user == null) { // not login

			log.debug("Security check for [{}] user not found. Access denied.", url);

			securityManager.onNotLogin(request, response);
			return false;

		} else { // logged in

			boolean success
					= checkRequireRoles(request, response, user, requireRoles);
			if(!success) {
				log.error("Access denied [{}]: requireRoles failed: values={}",
						url,
						StringUtils.join(requireRoles, ","));
				return false;
			}

			success = checkRequireAnyPrivileges(
					request, response, user, requireAnyPrivileges);
			if(!success) {
				log.error("Access denied [{}]: requireAnyPrivileges failed: values={}",
						url,
						StringUtils.join(requireAnyPrivileges, ","));
				return false;
			}

			success = checkRequireAllPrivileges(
					request, response, user, requireAllPrivileges);
			if(!success) {
				log.error("Access denied [{}]: requireAllPrivileges failed: values={}",
						url,
						StringUtils.join(requireAllPrivileges, ","));
				return false;
			}
		}

		log.debug(" Access granted [{}]: passed", url);
		return true;
	}

	private boolean checkRequireRoles(HttpServletRequest request, HttpServletResponse response,
	                                  TinyUser user, String[] requireRoles) {

		for(String requireRole : requireRoles) {
			for(Role userRole : user.getRoles()) {
				if(userRole.getId().equals(requireRole)) {
					return true;
				}
			}
		}

		securityManager.onRequireRolesFail(request, response, user, requireRoles);
		return false;
	}

	/**
	 * Checks items from <code>requireAnyPrivileges</code>.
	 * The check is passed if user has any of the privileges enumerated.
	 *
	 * @param req
	 * @param res
	 * @param user
	 * 		the current user
	 * @param privileges
	 * 		the privileges to check.
	 *
	 * @return <code>TRUE</code> if passed, and <code>FALSE</code> otherwise.
	 */
	private boolean checkRequireAnyPrivileges(HttpServletRequest req, HttpServletResponse res,
	                                          TinyUser user, String[] privileges) {

		if(privileges == null || privileges.length == 0) {
			return true;
		}

		boolean okay = false;
		for(String key : privileges) {

			if((user.getPrivilege().getValue(key)) > 0) {
				okay = true;
				break;
			}
		}
		if(!okay) {
			securityManager.onRequireAnyPrivilegeFail(req, res, user, privileges);
			return false;
		}
		return true;
	}


	/**
	 * Checks items from <code>requireAllPrivileges</code>.
	 * The check is passed if user has all of the privileges enumerated.
	 *
	 * @param req
	 * @param res
	 * @param user
	 * 		the current user
	 * @param privileges
	 * 		the privileges to check.
	 *
	 * @return <code>TRUE</code> if passed, and <code>FALSE</code> otherwise.
	 */
	private boolean checkRequireAllPrivileges(
			HttpServletRequest req, HttpServletResponse res,
			TinyUser user, String[] privileges) {

		if(privileges == null || privileges.length == 0) {
			return true;
		}

		if(privileges != null) {
			for(String key : privileges) {
				int value = user.getPrivilege().getValue(key);
				if(value <= 0) {
					securityManager.onRequireAllPrivilegesFail(req, res, user, privileges);
					return false;
				}
			}

		}
		return true;
	}


	@Override
	public final void postHandle(HttpServletRequest request, HttpServletResponse response,
	                             Object handler, ModelAndView modelAndView) throws Exception {

		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public final void afterCompletion(HttpServletRequest request, HttpServletResponse response,
	                                  Object handler, Exception ex) throws Exception {

		super.afterCompletion(request, response, handler, ex);
	}


}
