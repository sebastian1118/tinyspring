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
public abstract class TinySecurityInterceptor extends HandlerInterceptorAdapter {

	private static final Logger log = LoggerFactory.getLogger(TinySecurityInterceptor.class);


	protected abstract TinyAuthenticator getAuthenticator();

	protected abstract void onNotLogin(HttpServletRequest request,
	                                   HttpServletResponse response);

	protected abstract void onRequireAllPrivilegesFail(HttpServletRequest request,
	                                                   HttpServletResponse response);

	protected abstract void onRequireAnyPrivilegeFail(HttpServletRequest request,
	                                                  HttpServletResponse response);


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
				return checkAndRespond(url, request, response, methodAnnotation
								.requireAnyPrivileges(), methodAnnotation.requireAllPrivileges(),
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
				return checkAndRespond(url, request, response, classAnnotation
								.requireAnyPrivileges(), classAnnotation.requireAllPrivileges(),
						classAnnotation.stateless());
			}


		}

		return true; //if fall here, access will be granted
	}

	private final boolean checkAndRespond(String url,
	                                      HttpServletRequest request,
	                                      HttpServletResponse response,
	                                      String[] requireAnyPrivileges,
	                                      String[] requireAllPrivileges,
	                                      boolean stateless)
			throws ServletException, IOException {

		log.debug("Security check for [{}], requireAnyPrivileges[{}], requireAllPrivileges[{}]",
				url,
				StringUtils.join(requireAnyPrivileges, ","),
				StringUtils.join(requireAllPrivileges, ",")
		         );


		if(stateless) { // authenticate via request
			return getAuthenticator().authenticateStatelessly(request, response);
		}

		TinyUser user = (TinyUser) request.getSession()
		                                  .getAttribute(TinyAuthenticator.SESSION_NAME_USER);

		if(user == null) { // not login

			log.debug("Security check for [{}] user not found. Access denied.", url);

			//			request.setAttribute("notLogin", true);
			//			RequestDispatcher rd = request.getRequestDispatcher(accessDeniedUrl);
			//			rd.forward(request, response);
			onNotLogin(request, response);
			return false;

		} else { // logged in

			boolean success = checkRequireAnyPrivileges(
					request, response, user.getPrivilegeSet(), requireAnyPrivileges);
			if(!success) {
				log.error("Access denied [{}]: requireAnyPrivileges failed: values={}",
						url,
						StringUtils.join(requireAnyPrivileges, ","));
				return false;
			}

			success = checkRequireAllPrivileges(
					request, response, user.getPrivilegeSet(), requireAllPrivileges);
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

	private boolean checkRequireAnyPrivileges(HttpServletRequest req, HttpServletResponse res,
	                                          Privilege privilegeSet, String[] roles) {

		boolean okay = false;
		for(String key : roles) {

			if((privilegeSet.getValue(key)) > 0) {
				okay = true;
				break;
			}
		}
		if(!okay) {
			onRequireAnyPrivilegeFail(req, res);
			return false;
		}
		return true;
	}

	private boolean checkRequireAllPrivileges(
			HttpServletRequest req, HttpServletResponse res,
			Privilege privilegeSet, String[] privileges) {

		if(privileges != null) {
			for(String key : privileges) {
				int value = privilegeSet.getValue(key);
				if(value <= 0) {
					onRequireAllPrivilegesFail(req, res);
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
