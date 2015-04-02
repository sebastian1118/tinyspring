package org.triiskelion.tinyspring.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian MA
 * Date: August 11, 2014
 * Time: 15:40
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurityCheck {

	/**
	 * default value is true. set to false if you wish to override TYPE-level annotation. Only
	 * effective if annotated on METHOD.
	 *
	 * @return
	 */
	boolean value() default true;

	boolean stateless() default false;

	/**
	 * If set, security check is passed if ANY of the privileges is granted.
	 * <p>This is the notation of 'Role'.</p>
	 *
	 * @return array of privilege keys
	 */
	String[] requireAnyPrivileges() default {};

	/**
	 * If set, security check is passed only if ALL of the privileges is granted.
	 *
	 * @return array of privilege keys
	 */
	String[] requireAllPrivileges() default {};

	/**
	 * If set, only matched paths will be checked. Excluded path selected by <code>excludes()
	 * </code> will NOT be checked.
	 * <p>Only effective if annotated on TYPE.</p>
	 */
	String[] matches() default { "**" };

	/**
	 * matched paths will be excluded thus will NOT be checked. Only effective if annotated on
	 * TYPE.
	 */
	String[] excludes() default {};


}
