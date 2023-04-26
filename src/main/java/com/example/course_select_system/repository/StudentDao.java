package com.example.course_select_system.repository;

import com.example.course_select_system.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface StudentDao extends JpaRepository<Student, String> {
}