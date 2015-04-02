package org.triiskelion.tinyspring.security;

/**
 * @author Sebastian MA
 */
public class TinyUser {

	String username;

	String password;

	String rawPrivilege;

	Privilege privilegeSet;

	Object nestedEntity;

	public String getUsername() {

		return username;
	}

	public void setUsername(String username) {

		this.username = username;
	}

	public String getPassword() {

		return password;
	}

	public void setPassword(String password) {

		this.password = password;
	}

	public String getRawPrivilege() {

		return rawPrivilege;
	}

	public void setRawPrivilege(String rawPrivilege) {

		this.rawPrivilege = rawPrivilege;
	}

	public Privilege getPrivilegeSet() {

		return privilegeSet;
	}

	public void setPrivilegeSet(Privilege privilegeSet) {

		this.privilegeSet = privilegeSet;
	}

	public Object getNestedEntity() {

		return nestedEntity;
	}

	public void setNestedEntity(Object nestedEntity) {

		this.nestedEntity = nestedEntity;
	}
}
