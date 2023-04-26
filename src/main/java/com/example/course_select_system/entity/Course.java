package com.example.course_select_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "course")
public class Course {
  @Id
  @Column(name = "course_code")
  private String courseCode; // 課程代碼

  @Column(name = "course_name")
  private String courseName; // 課程名稱

  @Column(name = "day_fo_the_week")
  private int dayOfWeek; // 上課星期

  @Column(name = "start_time")
  private LocalTime startTime = LocalTime.of(00,00,00); // 上課開始時間

  @Column(name = "end_time")
  private LocalTime endTime = LocalTime.of(00,00,00); // 上課結束時間

  @Column(name = "credit")
  private int credits; // 學分

  @Column(name = "enrollment_count")
  private int enrollmentCount; // 已選人數


  public Course() {

  }

  public Course(String courseCode, String courseName, int dayOfWeek, LocalTime startTime, LocalTime endTime, int credits) {
    this.courseCode = courseCode;
    this.courseName = courseName;
    this.dayOfWeek = dayOfWeek;
    this.startTime = startTime;
    this.endTime = endTime;
    this.credits = credits;
  }

  public String getCourseCode() {
    return courseCode;
  }

  public void setCourseCode(String courseCode) {
    this.courseCode = courseCode;
  }

  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }

  public int getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfWeek(int dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }

  public int getCredits() {
    return credits;
  }

  public void setCredits(int credits) {
    this.credits = credits;
  }

  public int getEnrollmentCount() {
    return enrollmentCount;
  }

  public void setEnrollmentCount(int enrollmentCount) {
    this.enrollmentCount = enrollmentCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Course course = (Course) o;
    return dayOfWeek == course.dayOfWeek && credits == course.credits &&  Objects.equals(courseCode, course.courseCode) && Objects.equals(courseName, course.courseName) && Objects.equals(startTime, course.startTime) && Objects.equals(endTime, course.endTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(courseCode, courseName, dayOfWeek, startTime, endTime, credits);
  }
}
