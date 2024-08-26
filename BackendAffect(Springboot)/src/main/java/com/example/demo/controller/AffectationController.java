


package com.example.demo.controller;

import com.example.demo.model.Affectation;
import com.example.demo.model.Project;
import com.example.demo.model.Student;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/affectation")
public class AffectationController  implements EventListener {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProjectRepository projectRepository;


    public List<Student> getAllStudents() {
        return studentRepository.findAllByOrderByMoyenDesc();
    }
    public HashSet<Integer> Get_All_Projects_Id(){
        HashSet<Integer> project_set = new HashSet<>();
        for (Project p : projectRepository.findAll()){
            project_set.add(p.getId());
        }
        return project_set;
    }
    public PriorityQueue<Map.Entry<Double, Integer>> heapStudentSort() {
        PriorityQueue<Map.Entry<Double, Integer>> heap = new PriorityQueue<>(
                (a, b) -> a.getKey().compareTo(b.getKey())
        );

        for (Student s : getAllStudents()) {
            // Adding the negation of the student's grade to prioritize higher grades
            heap.add(new AbstractMap.SimpleEntry<>(-1 * s.getMoyen(), s.getId()));
        }

        return heap;
    }

    @GetMapping("/")
    public List<Affectation>  affectStudent() {
         List<Affectation> affectation_List= new ArrayList<>();
        HashSet<Integer>  project_IDS=Get_All_Projects_Id();
        for  (Student s: getAllStudents()){
            for  (Project p: s.getProjects()){
                if (project_IDS.contains(p.getId())  ){
                    project_IDS.remove(p.getId());
                    affectation_List.add(new Affectation(s.getNom(),p.getName()));
                    break;
                }
            }
        }
        return affectation_List;
    }



}
