package org.triiskelion.tinyspring.security;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;

/**
 * @author Sebastian MA
 */
public class Privilege {

	private static final Logger log = LoggerFactory.getLogger(Privilege.class);

	public static int getPrivilege(PrivilegeSet set, String key) {

		if(set == null) {
			throw new RuntimeException("PrivilegeSet is null.");
		}
		if(StringUtils.isBlank(key)) {
			throw new RuntimeException("privilege key is blank.");
		}
		return getPrivilege(set, StringUtils.split(key, "."), 0);
	}

	/**
	 * @param privilegeSet
	 * 		the privilege set to be checked
	 * @param key
	 * 		array of keys
	 * @param index
	 * 		current position of the key array
	 *
	 * @return
	 */
	private static int getPrivilege(PrivilegeSet privilegeSet, String[] key, int index) {

		if(privilegeSet == null) {
			return -1;
		}
		if(index == key.length - 1) {
			PrivilegeItem item = privilegeSet.getItems().get(key[index]);
			if(item == null) {
				log.error("privilege {} not found", StringUtils.join(key, "."));
				return -1;
			} else {
				return item.getValue();
			}
		} else if(index < key.length - 1) {

			return getPrivilege(privilegeSet.getSubsets().get(key[index]),
					key, index + 1);
		} else {
			throw new RuntimeException("Should never get here.");
		}
	}

	/**
	 * Merge two privilege set
	 *
	 * @param dest
	 * @param privilegeString
	 *
	 * @return
	 */
	public static PrivilegeSet merge(PrivilegeSet dest, String privilegeString) {

		return merge(dest, parsePrivilegeSet(privilegeString));
	}

	/**
	 * Creates privilege set from json string.if multiple privilege sets are present,
	 * they will be merged into one.
	 *
	 * @param privilegeSet
	 * 		the privilege sets to parse
	 *
	 * @return the parsed and merged PrivilegeSet instance
	 */
	public static PrivilegeSet parsePrivilegeSet(String... privilegeSet) {

		if(privilegeSet == null || privilegeSet.length == 0) {
			return null;
		}

		ArrayList<PrivilegeSet> list = new ArrayList<>();
		for(String json : privilegeSet) {
			PrivilegeSet set = JSONObject.parseObject(json, PrivilegeSet.class);
			list.add(set);
		}

		//merge them into one
		PrivilegeSet finalResult = new PrivilegeSet();
		for(PrivilegeSet set : list) {
			merge(finalResult, set);
		}
		return finalResult;
	}

	/**
	 * Merges two privilege set into the destination. the original set will not be modified.
	 *
	 * @param dest
	 * 		the merged privilege set
	 * @param origin
	 * 		the privilege set to merge
	 *
	 * @return
	 */
	static PrivilegeSet merge(PrivilegeSet dest, PrivilegeSet origin) {

		BeanUtils.copyProperties(dest, origin);

		if(dest != null && dest.getItems() != null
				&& origin != null && origin.getItems() != null) {
			for(String key : origin.getItems().keySet()) {
				if(dest.getItems().get(key) != null) {
					dest.getItems().get(key).setValue(Math.max(dest.getItems().get(key).getValue(),
									origin.getItems().get(key).getValue())
					                                 );
				} else {
					dest.getItems().put(key, new PrivilegeItem(origin.getItems().get(key)));
				}
			}
		}

		if(dest != null && dest.getSubsets() != null
				&& origin != null && origin.getSubsets() != null) {
			for(String key : origin.getSubsets().keySet()) {
				if(dest.getSubsets().get(key) == null) {
					dest.getSubsets().put(key, new PrivilegeSet());
				}
				merge(dest.getSubsets().get(key), origin.getSubsets().get(key));
			}
		}

		return dest;
	}
}
