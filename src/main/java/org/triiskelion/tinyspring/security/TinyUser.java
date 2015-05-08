package org.triiskelion.tinyspring.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sebastian MA
 */
public class TinyUser {

	protected String username;

	protected Set<Role> roles;

	protected Privileges privilege = new Privileges();

	protected Object nestedEntity;

	/**
	 * @param username
	 * 		user's login
	 * @param roles
	 * 		user's roles
	 * @param nestedEntity
	 * 		entity to associate
	 */
	public TinyUser(String username, Collection<Role> roles, Object nestedEntity) {

		this.username = username;
		this.roles = new HashSet<>(roles);
		this.nestedEntity = nestedEntity;

		for(Role role : roles) {
			this.privilege = privilege.merge(role.getPrivilege());
		}
	}

	public boolean hasRole(String roleId) {

		if(roleId == null || roleId.isEmpty()) {
			return false;
		}

		for(Role role : roles) {
			if(role.getId().equals(roleId)) {
				return true;
			}
		}
		return false;
	}

	public int hasPrivilege(String key) {

		return this.privilege.getValue(key);
	}

	/**
	 * @return this user's roles
	 */
	public Set<Role> getRoles() {

		return roles;
	}

	/**
	 * This user's privileges. The result is the privileges from all the roles of the user
	 * merged together.
	 *
	 * @return this user's privileges
	 */
	public Privileges getPrivilege() {

		return privilege;
	}


	/**
	 * @return user's login
	 */
	public String getUsername() {

		return username;
	}

	/**
	 * @return entity associated to this user
	 */
	public Object getNestedEntity() {

		return nestedEntity;
	}
}
