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
 * Privilege
 *
 * @author Sebastian
 */
public class Privilege implements Cloneable {

	private static final Logger log = LoggerFactory.getLogger(Privilege.class);

	String name = "";

	String description = "";

	int value = -1;

	Map<String, Privilege> items = new HashMap<>();

	Map<String, Privilege> subsets = new HashMap<>();

	public Privilege() {

	}

	public Privilege(Privilege clone) {

		this.name = clone.name;
		this.description = clone.description;
		this.value = clone.value;
	}

	public Privilege(String name, String description) {

		this.name = name;
		this.description = description;
	}

	public Privilege(String name, String description, int value) {

		this.name = name;
		this.description = description;
		this.value = value;
	}

	public Map<String, Privilege> getItems() {

		return items;
	}

	public void setItems(Map<String, Privilege> items) {

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


	public Map<String, Privilege> getSubsets() {

		return subsets;
	}

	public void setSubsets(Map<String, Privilege> subsets) {

		this.subsets = subsets;
	}

	public String toString() {

		return JSONObject.toJSONString(this, true);
	}

	public Privilege clone() {

		Privilege result = new Privilege();
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

	public Privilege merge(Privilege set) {

		if(set == null) {
			throw new IllegalArgumentException();
		}

		Privilege result = this.clone();
		if(result == null) {
			throw new RuntimeException();
		}

		if(result.getItems() != null && set.getItems() != null) {
			for(String key : set.getItems().keySet()) {
				if(result.getItems().get(key) != null) {
					result.getItems().get(key).setValue(
							Math.max(result.getItems().get(key).getValue(),
									set.getItems().get(key).getValue())
					                                   );
				} else {
					result.getItems().put(key, new Privilege(set.getItems().get(key)));
				}
			}
		}

		if(result.getSubsets() != null && set.getSubsets() != null) {
			for(String key : set.getSubsets().keySet()) {
				if(result.getSubsets().get(key) == null) {
					result.getSubsets().put(key, new Privilege());
				}
				result.getSubsets().put(key,
						result.getSubsets().get(key).merge(set.getSubsets().get(key)));
			}
		}

		return result;
	}

	public static Privilege parse(@Nullable String... privilegeSet) {

		if(privilegeSet == null || privilegeSet.length == 0) {
			return null;
		}

		ArrayList<Privilege> list = new ArrayList<>();
		for(String json : privilegeSet) {
			Privilege set = JSONObject.parseObject(json, Privilege.class);
			list.add(set);
		}

		//merge them into one
		Privilege finalResult = new Privilege();
		for(Privilege set : list) {
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

	protected static int getValue(Privilege set, @NotNull String[] key, int index) {

		if(index == key.length - 1) { // position
			Privilege item = set.getItems().get(key[index]);
			if(item == null) {
				log.warn("privilege {} not found", StringUtils.join(key, "."));
				return -1;
			} else {
				return item.getValue();
			}
		} else if(index < key.length - 1) {

			Privilege subset = set.getSubsets().get(key[index]);
			return subset == null ? -1 : getValue(set, key, index + 1);
		}

		throw new RuntimeException("Should never getValue here.");
	}
}
