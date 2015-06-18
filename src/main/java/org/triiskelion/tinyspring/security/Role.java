package org.triiskelion.tinyspring.security;

/**
 * A role is a set of privileges defining the capabilities of certain group of user.
 * Users can have multiple roles, in this case his privileges should be the ones from all the
 * roles he owns merged together.
 *
 * @author Sebastian MA
 */
public class Role {

	String id;

	Privileges privilege;

	public Role(String id, Privileges privilege) {

		this.id = id;
		this.privilege = privilege;
	}

	/**
	 * @return The unique id of this role.
	 */
	public String getId() {

		return id;
	}

	/**
	 * @return this role's privileges.
	 */
	public Privileges getPrivilege() {

		return privilege;
	}

}
