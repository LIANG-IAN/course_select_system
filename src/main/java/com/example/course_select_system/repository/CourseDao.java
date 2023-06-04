package com.example.course_select_system.repository;

import com.example.course_select_system.entity.Course;
import com.example.course_select_system.vo.CourseResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CourseDao extends JpaRepository<Course, String> {

  public List<Course> findCourseByCourseName(String courseName);
  public List<Course> findAll();

  @Query(value = "select * from course_select_system.course",nativeQuery = true)
  public List<Course> findCourseAll();

}

