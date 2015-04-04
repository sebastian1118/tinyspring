package org.triiskelion.tinyspring.viewmodel;

/**
 * @author Sebastian MA
 */
public interface Mapper<S, D> {

	D map(S obj);

}
