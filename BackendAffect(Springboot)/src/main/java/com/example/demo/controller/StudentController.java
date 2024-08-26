package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.NumberOfProjectsForEachStudent;
import com.example.demo.model.Project;
import com.example.demo.model.Student;
import com.example.demo.repository.NumberOfProjectStudentRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private NumberOfProjectStudentRepository numStPr ;

    // NumberOfProjectsForEachStudent CRUD
    @EventListener(ApplicationReadyEvent.class)
    public void addColumnAuto() {
        try {
            int num = (int) numStPr.count();
            if (num == 0) {
                NumberOfProjectsForEachStudent numb = new NumberOfProjectsForEachStudent();
                numb.setNum(4);
                numStPr.save(numb);
            }
        } catch (Exception e) {
            // Log the exception or handle it accordingly
            e.printStackTrace();
        }
    }
    @PutMapping("/max_project")
    public ResponseEntity<NumberOfProjectsForEachStudent> updateNumPrSt( @RequestBody NumberOfProjectsForEachStudent numPrStudent){
        NumberOfProjectsForEachStudent  numPrS = numStPr.findById(1)
                .orElseThrow(() -> new ResourceNotFoundException("Project not exist with id :" + 1));

        numPrS.setNum(numPrStudent.getNum());
        NumberOfProjectsForEachStudent n = numStPr.save(numPrS);
        return ResponseEntity.ok(n);
    }
    @GetMapping("/max_project")
    public int  GetNumberOfPrST(){

        return	numStPr.findAll().get(0).getNum();

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

        student.setMoyen(studentDetails.getMoyen());
        student.setNom(studentDetails.getNom());

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

        if (student == null || project == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            student.addProject(project,GetNumberOfPrST());
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
}
