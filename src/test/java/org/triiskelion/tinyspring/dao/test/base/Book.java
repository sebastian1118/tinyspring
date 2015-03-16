package org.triiskelion.tinyspring.dao.test.base;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian MA
 * Date: December 05, 2014
 * Time: 11:24
 */
@Entity
@Table(name = "user")
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private int id;

	@Column
	private String title;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", nullable = true)
	private User user;

	public int getId() {

		return id;
	}

	public void setId(int id) {

		this.id = id;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public User getUser() {

		return user;
	}

	public void setUser(User user) {

		this.user = user;
	}
}
