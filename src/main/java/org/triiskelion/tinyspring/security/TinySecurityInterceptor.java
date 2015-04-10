package org.triiskelion.tinyspring.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian MA
 * Date: August 10, 2014
 * Time: 15:02
 */
public class TinySecurityInterceptor extends HandlerInterceptorAdapter {

	private static final Logger log = LoggerFactory.getLogger(TinySecurityInterceptor.class);

	private String accessDeniedUrl;

	private String privilegeDeniedUrl;

	TinyAuthenticator authenticator;

	public TinyAuthenticator getAuthenticator() {

		return authenticator;
	}

	public void setAuthenticator(TinyAuthenticator authenticator) {

		this.authenticator = authenticator;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object
			handler) throws Exception {

		if(handler instanceof ResourceHttpRequestHandler) {
			return true;
		}

		if(handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;

			SecurityCheck classAnnotation
					= handlerMethod.getMethod().getDeclaringClass().getAnnotation(SecurityCheck
					.class);
			SecurityCheck methodAnnotation = handlerMethod.getMethodAnnotation(SecurityCheck
					.class);
			String entry = request.getRequestURI().replace(request.getContextPath(), "");

			if(methodAnnotation != null) {

				if(!methodAnnotation.value()) {
					log.debug("Security Check for [{}] disabled. Access granted.", entry);
				} else {
					return checkAndRespond(entry, request, response, methodAnnotation);
				}
			} else {
				if(classAnnotation == null) {
					log.debug("No @SecurityCheck found for [{}]. Access granted.", entry);
				} else {
					String[] matches = classAnnotation.matches();
					String[] excludes = classAnnotation.excludes();

					boolean requireCheck = false;

					for(String match : matches) {
						if(match.contains("**")) {
							match = match.replace("**", ".*");
						} else {
							match = match.replace("*", "[^/]*");
						}
						if(entry.matches(match)) {
							requireCheck = true;
						} else {
							log.debug("SecurityCheck for [{}] not matched. Access granted.",
									entry);
						}

						if(requireCheck) {
							for(String exclude : excludes) {
								if(exclude.contains("**")) {
									exclude = exclude.replace("**", ".*");
								} else {
									exclude = exclude.replace("*", "[^/]*");
								}
								exclude = ".*" + exclude + ".*";
								if(entry.matches(exclude)) {
									requireCheck = false;
									log.debug("SecurityCheck for [{}] excluded. Access granted.",
											entry);
									break;
								}
							}
						}
					}
					if(requireCheck) {

						return checkAndRespond(entry, request, response, classAnnotation);

					}


				}
			}
		}

		return true;
	}

	private boolean checkAndRespond(String entry, HttpServletRequest request,
	                                HttpServletResponse response,
	                                SecurityCheck annotation) throws
			ServletException, IOException {

		log.debug("Security check for [{}], require roles [{}], privileges [{}]", entry,
				StringUtils.join(annotation.requireRole(), ","), annotation.requirePrivilege());

		if(annotation.stateless()) {//check from header
			return authenticator.authenticateStatelessly(request, response);

		} else {
			TinyUser user =
					(TinyUser) request.getSession()
					                  .getAttribute(TinyAuthenticator.SESSION_NAME_USER);

			if(user == null) {

				log.debug("Security check for [{}] user not found. Access denied.", entry);
				request.setAttribute("notLogin", true);
				RequestDispatcher rd = request.getRequestDispatcher(accessDeniedUrl);
				rd.forward(request, response);
				return false;

			} else {

				String key = annotation.requirePrivilege();
				if(key != null && !key.isEmpty()) {
					int value = Privilege.getPrivilege(user.getPrivilegeSet(), key);
					if(value <= 0) {
						log.debug("Security check for [{}] privilege[{}] failed. Access denied.",
								entry, key);
						RequestDispatcher rd = request.getRequestDispatcher(privilegeDeniedUrl);
						rd.forward(request, response);
						return false;
					}
				}
			}
		}

		log.debug("Security check for [{}] finished, Access granted.", entry);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
	                       Object handler, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
	                            Object handler, Exception ex) throws Exception {

	}

	public String getAccessDeniedUrl() {

		return accessDeniedUrl;
	}

	public void setAccessDeniedUrl(String accessDeniedUrl) {

		this.accessDeniedUrl = accessDeniedUrl;
	}

	public String getPrivilegeDeniedUrl() {

		return privilegeDeniedUrl;
	}

	public void setPrivilegeDeniedUrl(String privilegeDeniedUrl) {

		this.privilegeDeniedUrl = privilegeDeniedUrl;
	}
}
