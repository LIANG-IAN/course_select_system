package com.example.course_select_system.vo;

import com.example.course_select_system.entity.Course;
import com.example.course_select_system.entity.Student;

import java.util.List;

public class CourseResponse {
  private List<Course> courseList;

  private Course course;

  private Student student;

  private String message;

  public CourseResponse() {

  }

  public CourseResponse(List<Course> courseList) {
    this.courseList = courseList;
  }

  public CourseResponse(String message) {
    this.message = message;
  }

  public CourseResponse(Student student, String message) {
    this.message = message;
    this.student = student;
  }


  public CourseResponse(List<Course> courseList, String message) {
    this.courseList = courseList;
    this.message = message;
  }

  public CourseResponse(Course course, String message) {
    this.course = course;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public List<Course> getCourseList() {
    return courseList;
  }

  public void setCourseList(List<Course> courseList) {
    this.courseList = courseList;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
  }
}



