package com.example.demo.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Student")
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "name")
	private String name;

	@Column(name = "average")
	private double average;

	@ManyToMany(cascade = {CascadeType.ALL, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})

	@JoinTable(
			name = "student_project",
			joinColumns = @JoinColumn(name = "student_id"),
			inverseJoinColumns = @JoinColumn(name = "project_id")
	)
	@OrderColumn(name = "project_order") // This ensures the order of projects is maintained in the database
	private List<Project> projects = new ArrayList<>();

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

	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public List<Project> getProjects() {
		return projects;
	}



	public void addProject(Project project,int number) {
		if (projects.size() >= number) {
			throw new IllegalStateException("A student can only have up to 3 projects.");
		}
		projects.add(project);
	}

	public void removeProject(Project project) {
		projects.remove(project);
	}
}
