package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Project;

import javax.transaction.Transactional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @Modifying
    @Transactional // Ensure this method runs within a transaction
    @Query(value = "DELETE FROM student_project WHERE project_id = ?1", nativeQuery = true)
    void deleteFromStudentProjectByProjectId(Integer projectId);
}
