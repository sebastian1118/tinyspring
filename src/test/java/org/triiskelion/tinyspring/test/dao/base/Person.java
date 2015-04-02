package org.triiskelion.tinyspring.test.dao.base;

import javax.persistence.*;

/**
 * @author Sebastian MA
 */
@Entity
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private int id;

	@Column
	private String name;

	public int getId() {

		return id;
	}

	public void setId(int id) {

		this.id = id;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}
}
