package com.example.course_select_system.repository;

import com.example.course_select_system.entity.StudentCourseCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StudentCourseCodesDao extends JpaRepository<StudentCourseCodes, Integer> {

  public List<StudentCourseCodes> findCourseCodesByStudentId(String studentId);

  public List<StudentCourseCodes> findByStudentId(String studentId);

  public StudentCourseCodes findByStudentIdAndCourseCode(String studentId, String courseCode);

}
