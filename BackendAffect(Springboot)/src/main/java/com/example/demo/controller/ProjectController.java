package com.example.demo.controller;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Project;
import com.example.demo.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/projects")
public class ProjectController implements EventListener {

	@Autowired
	private ProjectRepository projectRepository;

	@GetMapping("/")
	public List<Project> getAllProjects() {
		return projectRepository.findAll();
	}

	@PostMapping("/")
	public Project createProject(@RequestBody Project project) {
		return projectRepository.save(project);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Project> getProjectById(@PathVariable int id) {
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not exist with id :" + id));
		return ResponseEntity.ok(project);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Project> updateProject(@PathVariable Integer id, @RequestBody Project projectDetails) {
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not exist with id :" + id));

		project.setName(projectDetails.getName());
		Project updatedProject = projectRepository.save(project);
		return ResponseEntity.ok(updatedProject);
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<Map<String, Boolean>> deleteProject(@PathVariable Integer id) {
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not exist with id :" + id));
		projectRepository.deleteFromStudentProjectByProjectId(id);
		projectRepository.delete(project);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/all")
	@Transactional
	public ResponseEntity<Map<String, Boolean>> deleteAllProjects() {
		projectRepository.deleteAll();
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return ResponseEntity.ok(response);
	}
}
