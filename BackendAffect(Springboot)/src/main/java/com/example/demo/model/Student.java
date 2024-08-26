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

	@Column(name = "nom")
	private String nom;

	@Column(name = "moyen")
	private double moyen;

	@ManyToMany
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

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public double getMoyen() {
		return moyen;
	}

	public void setMoyen(double moyen) {
		this.moyen = moyen;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		if (projects.size() > 3) {
			throw new IllegalArgumentException("A student can be associated with a maximum of 3 projects.");
		}
		this.projects = projects;
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
