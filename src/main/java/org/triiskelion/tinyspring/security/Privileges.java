package org.triiskelion.tinyspring.security;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Set of privileges defining the capabilities of one role or one user.
 *
 * @author Sebastian
 */
public class Privileges implements Cloneable {

	private static final Logger log = LoggerFactory.getLogger(Privileges.class);

	String name = "";

	String description = "";

	int value = -1;

	Map<String, Privileges> items = new HashMap<>();

	Map<String, Privileges> subsets = new HashMap<>();

	public Privileges() {

	}

	public Privileges(String name, String description) {

		this.name = name;
		this.description = description;
	}

	public Privileges(String name, String description, int value) {

		this.name = name;
		this.description = description;
		this.value = value;
	}

	public Map<String, Privileges> getItems() {

		return items;
	}

	public void setItems(Map<String, Privileges> items) {

		this.items = items;
	}

	public int getValue() {

		return value;
	}

	public void setValue(int value) {

		this.value = value;
	}


	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getDescription() {

		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}


	public Map<String, Privileges> getSubsets() {

		return subsets;
	}

	public void setSubsets(Map<String, Privileges> subsets) {

		this.subsets = subsets;
	}

	public String toString() {

		return JSONObject.toJSONString(this, true);
	}

	public Privileges clone() {

		Privileges result = new Privileges();
		result.name = this.name;
		result.description = this.description;
		result.value = this.value;

		for(String key : this.getItems().keySet()) {
			result.getItems().put(key, this.getItems().get(key).clone());
		}
		for(String key : this.getSubsets().keySet()) {
			result.getItems().put(key, this.getSubsets().get(key).clone());
		}
		return result;
	}

	/**
	 * Returns a copy of this instance merged with another one.<p>
	 * This instance is immutable and unaffected by this method call.
	 *
	 * @param privilege
	 * 		another privilege instance
	 *
	 * @return a copy of this instance merged with another
	 */
	public Privileges merge(Privileges privilege) {

		if(privilege == null) {
			throw new IllegalArgumentException();
		}

		Privileges result = this.clone();
		if(result == null) {
			throw new RuntimeException();
		}

		if(result.getItems() != null && privilege.getItems() != null) {
			for(String key : privilege.getItems().keySet()) {
				if(result.getItems().get(key) != null) {
					result.getItems().get(key).setValue(
							Math.max(result.getItems().get(key).getValue(),
									privilege.getItems().get(key).getValue())
					                                   );
				} else {
					result.getItems().put(key, privilege.getItems().get(key).clone());
				}
			}
		}

		if(result.getSubsets() != null && privilege.getSubsets() != null) {
			for(String key : privilege.getSubsets().keySet()) {
				if(result.getSubsets().get(key) == null) {
					result.getSubsets().put(key, new Privileges());
				}
				result.getSubsets().put(key,
						result.getSubsets().get(key).merge(privilege.getSubsets().get(key)));
			}
		}

		return result;
	}

	public static Privileges parse(@Nullable String... privilegeSet) {

		if(privilegeSet == null || privilegeSet.length == 0) {
			return null;
		}

		ArrayList<Privileges> list = new ArrayList<>();
		for(String json : privilegeSet) {
			Privileges set = JSONObject.parseObject(json, Privileges.class);
			list.add(set);
		}

		//merge them into one
		Privileges finalResult = new Privileges();
		for(Privileges set : list) {
			finalResult.merge(set);
		}
		return finalResult;
	}

	/**
	 * Retrieve a privilege from an privilege set.
	 *
	 * @param key
	 * 		the	key of the privilege
	 *
	 * @return value of the privilege or -1 if not found
	 */
	public int getValue(@NotNull String key) {

		if(StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("privilege key is blank.");
		}
		String[] keys = StringUtils.split(key, ".");
		if(keys == null) {
			throw new IllegalArgumentException("Could not split key: " + key);
		}
		return getValue(this, keys, 0);
	}

	protected static int getValue(Privileges set, @NotNull String[] key, int index) {

		if(index == key.length - 1) { // position
			Privileges item = set.getItems().get(key[index]);
			if(item == null) {
				log.warn("privilege {} not found", StringUtils.join(key, "."));
				return -1;
			} else {
				return item.getValue();
			}
		} else if(index < key.length - 1) {

			Privileges subset = set.getSubsets().get(key[index]);
			return subset == null ? -1 : getValue(set, key, index + 1);
		}

		throw new RuntimeException("Should never getValue here.");
	}
}
