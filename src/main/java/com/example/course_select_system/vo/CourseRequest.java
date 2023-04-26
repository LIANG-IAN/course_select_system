package com.example.course_select_system.vo;

import com.example.course_select_system.entity.Course;
import com.example.course_select_system.entity.Student;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;


public class CourseRequest {
  @JsonProperty("course_code_list")
  private List<String> courseCodeList;

  @JsonProperty("course")
  private Course course;

  @JsonProperty("student")
  private Student student;

  @JsonProperty("course_code")
  private String courseCode;

  @JsonProperty("course_name")
  private String courseName;

  @JsonProperty("day_of_week")
  private int dayOfWeek;

  @JsonProperty("start_time")
  private LocalTime startTime;

  @JsonProperty("end_time")
  private LocalTime endTime;

  @JsonProperty("credits")
  private int credits;

  @JsonProperty("enrollment_count")
  private int enrollmentCount;

  @JsonProperty("message")
  private String message;

  @JsonProperty("student_id")
  private String studentId;

  @JsonProperty("student_name")
  private String studentName;

  public Course getCourse() {
    return course;
  }

  public Student getStudent() {
    return student;
  }

  public List<String> getCourseCodeList() {
    return courseCodeList;
  }

  public String getCourseCode() {
    return courseCode;
  }

  public String getCourseName() {
    return courseName;
  }

  public int getDayOfWeek() {
    return dayOfWeek;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public int getCredits() {
    return credits;
  }

  public int getEnrollmentCount() {
    return enrollmentCount;
  }

  public String getMessage() {
    return message;
  }

  public String getStudentId() {
    return studentId;
  }

  public String getStudentName() {
    return studentName;
  }
}
