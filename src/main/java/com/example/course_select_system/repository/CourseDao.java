package com.example.course_select_system.repository;

import com.example.course_select_system.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CourseDao extends JpaRepository<Course, String> {

  public List<Course> findCourseByCourseName(String courseName);


}

