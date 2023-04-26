package com.example.course_select_system.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "student_course_codes")
public class StudentCourseCodes {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "student_id")
  private String studentId;

  @Column(name = "course_code")
  private String courseCode;


  public StudentCourseCodes() {
  }

  public StudentCourseCodes(String studentId, String courseCode) {
    this.studentId = studentId;
    this.courseCode = courseCode;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  public String getCourseCode() {
    return courseCode;
  }

  public void setCourseCode(String courseCode) {
    this.courseCode = courseCode;
  }
}
