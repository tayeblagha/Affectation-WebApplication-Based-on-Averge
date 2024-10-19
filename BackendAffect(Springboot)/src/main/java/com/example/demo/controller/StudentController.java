package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Project;
import com.example.demo.model.Student;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProjectRepository projectRepository;
    // Static variable to store the max number of projects per student
    private static int maxProjectsPerStudent = 3;  // default value

    // Update the max number of projects
    @PutMapping("/max_project")
    public ResponseEntity<Integer> updateMaxProjects(@RequestBody int newMaxProjects) {
        maxProjectsPerStudent = newMaxProjects;  // update static variable
        return ResponseEntity.ok(maxProjectsPerStudent);
    }

    // Get the current max number of projects
    @GetMapping("/max_project")
    public ResponseEntity<Integer> getMaxProjects() {
        return ResponseEntity.ok(maxProjectsPerStudent);
    }

    @GetMapping("/")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @PostMapping("/")
    public Student createStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not exist with id :" + id));
        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Integer id, @RequestBody Student studentDetails) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not exist with id :" + id));

        student.setName(studentDetails.getName());
        student.setAverage(studentDetails.getAverage());

        Student updatedStudent = studentRepository.save(student);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteStudent(@PathVariable Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not exist with id :" + id));

        studentRepository.delete(student);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/all")
    public ResponseEntity<Map<String, Boolean>> deleteAllStudents() {
        studentRepository.deleteAll();
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }


    // Add project to student
    @PostMapping("/{studentId}/projects/{projectId}")
    public ResponseEntity<Student> addProjectToStudent(@PathVariable int studentId, @PathVariable int projectId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        Project project = projectRepository.findById(projectId).orElse(null);
        if (student == null || project == null ) {
            return ResponseEntity.notFound().build();
        }
        HashSet<Integer> projectIds = new HashSet<>();
        for (Project p : student.getProjects()) {
            projectIds.add(p.getId());
        }

        // Check if the project already exists for the student
        if (projectIds.contains(projectId)) {
            return ResponseEntity.status(400).body(student); // Project already added
        }

        try {
            student.addProject(project, maxProjectsPerStudent);
            studentRepository.save(student);
            return ResponseEntity.ok(student);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Get projects for a student
    @GetMapping("/{studentId}/projects")
    public ResponseEntity<List<Project>> getStudentProjects(@PathVariable int studentId) {
        return studentRepository.findById(studentId)
                .map(student -> ResponseEntity.ok(student.getProjects()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Remove project from student
    @DeleteMapping("/{studentId}/projects/{projectId}")
    public ResponseEntity<Student> removeProjectFromStudent(@PathVariable int studentId, @PathVariable int projectId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        Project project = projectRepository.findById(projectId).orElse(null);
        if (student == null || project == null) {
            return ResponseEntity.notFound().build();
        }
        student.removeProject(project);
        studentRepository.save(student);
        return ResponseEntity.ok(student);
    }
    // Find Available Projects for Student
    // Get projects for a student
    public HashSet<Integer> Get_All_Projects_Id(){
        HashSet<Integer> project_set = new HashSet<>();
        for (Project p : projectRepository.findAll()){
            project_set.add(p.getId());
        }
        return project_set;
    }
    @GetMapping("/{studentId}/availableprojects")
    public ResponseEntity<List<Project>> getStudentAvailableProjects(@PathVariable int studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null ) {
            return ResponseEntity.notFound().build();
        }

        if ( student.getProjects().size() >= maxProjectsPerStudent ) {
            return null;
        }
        HashSet<Integer> project_set = new HashSet<>();
        for (Project p :  student.getProjects()) {
            project_set.add(p.getId());
        }
        List<Project> allProject = projectRepository.findAll();
        List<Project> availableProjects = new ArrayList<Project>();

        for (Project p : allProject) {
            if (project_set.contains(p.getId())) {
                continue;
            }
            availableProjects.add(p);
        }
        return ResponseEntity.ok(availableProjects);
    }

    @GetMapping("/affectation")
    public Map<String,String>  affectStudent() {
        Map<String,String> affectationList = new HashMap<String,String>();
        HashSet<Integer>  project_IDS= projectRepository.findAll()
                .stream()
                .map(Project::getId)
                .collect(Collectors.toCollection(HashSet::new));

        for  (Student s: getAllStudents()){
            for  (Project p: s.getProjects()){
                if (project_IDS.contains(p.getId())  ){
                    project_IDS.remove(p.getId());
                    affectationList.put(s.getName(),p.getName());
                    break;
                }
            }
        }
        return affectationList;
    }
}
