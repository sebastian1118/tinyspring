package org.triiskelion.tinyspring.dao;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian MA
 * Date: February 10, 2015
 * Time: 12:12
 */
public class TinyEntity {

	Class entityClass;

	String alias;

	public TinyEntity(Class entityClass, String alias) {

		this.entityClass = entityClass;
		this.alias = alias;
	}

	public Class getEntityClass() {

		return entityClass;
	}

	public void setEntityClass(Class entityClass) {

		this.entityClass = entityClass;
	}

	public String getAlias() {

		return alias;
	}

	public void setAlias(String alias) {

		this.alias = alias;
	}
}
