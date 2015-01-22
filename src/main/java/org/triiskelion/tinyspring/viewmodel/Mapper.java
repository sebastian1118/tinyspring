package org.triiskelion.tinyspring.viewmodel;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian MA
 * Date: December 07, 2014
 * Time: 12:56
 */
public interface Mapper<O, D> {

	public D map(O obj);

}
