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

	String[] requireRole() default { };

	String requirePrivilege() default "";

	/**
	 * If set, only matched paths will be checked unless it is excluded. Only effective if
	 * annotated on TYPE.
	 *
	 * @return
	 */
	String[] matches() default { "**" };

	/**
	 * matched paths will be excluded thus will NOT be checked. Only effective if annotated on
	 * TYPE.
	 *
	 * @return
	 */
	String[] excludes() default { };


}
