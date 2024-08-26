package com.example.demo.model;

import javax.persistence.*;

@Entity
@Table(name="Project")
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name="name")
	private String name;



	// Getters and Setters
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



	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + "]";
	}

	public Project(String name) {
		super();
		this.name = name;
	}

	public Project() {
		super();
	}
}
