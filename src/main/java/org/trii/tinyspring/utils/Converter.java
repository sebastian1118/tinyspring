package org.trii.tinyspring.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian MA
 * Date: July 04, 2014
 * Time: 9:17
 *
 * @author tian
 * @version $Id: $Id
 */
public class Converter {

	/**
	 * <p>toIntArray.</p>
	 *
	 * @param string a {@link java.lang.String} object.
	 * @param separatorChars a {@link java.lang.String} object.
	 * @return an array of int.
	 */
	public static int[] toIntArray(String string, String separatorChars) {

		return toIntArray(StringUtils.split(string, separatorChars));
	}

	/**
	 * <p>toIntArray.</p>
	 *
	 * @param array an array of {@link java.lang.String} objects.
	 * @return an array of int.
	 */
	public static int[] toIntArray(String[] array) {

		int[] result = new int[array.length];
		for(int i = 0; i < array.length; i++) {
			result[i] = Integer.parseInt(array[i]);
		}
		return result;
	}
}
