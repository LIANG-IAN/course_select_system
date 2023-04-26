package com.example.course_select_system.controller;

import com.example.course_select_system.service.ifs.CourseService;
import com.example.course_select_system.vo.CourseRequest;
import com.example.course_select_system.vo.CourseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Result;


@RestController
public class CourseController {
  @Autowired
  private CourseService courseService;

  @PostMapping(value = "add_course")
  public CourseResponse addCourse(@RequestBody CourseRequest courseRequest) {
    return courseService.addCourse(courseRequest.getCourse());
  }

  @PostMapping(value = "update_course")
  public CourseResponse updateCourse(@RequestBody CourseRequest courseRequest) {
    return courseService.updateCourse(courseRequest.getCourse());
  }

  @PostMapping(value = "delete_course")
  public CourseResponse deleteCourse(@RequestBody CourseRequest courseRequest) {
    return courseService.deleteCourse(courseRequest.getCourseCode());
  }

  @PostMapping(value = "select_course")
  public CourseResponse selectCourse(@RequestBody CourseRequest courseRequest) {
    return courseService.selectCourse(courseRequest.getCourseCodeList(),courseRequest.getStudentId());
  }

  @PostMapping(value = "drop_course")
  public CourseResponse dropCourse(@RequestBody CourseRequest courseRequest) {
    return courseService.dropCourse(courseRequest.getCourseCode(),courseRequest.getStudentId());
  }

  @PostMapping(value = "delete_student")
  public CourseResponse deleteStudent(@RequestBody CourseRequest courseRequest) {
    return courseService.deleteStudent(courseRequest.getStudentId());
  }

  @PostMapping(value = "find_course_by_course_name")
  public CourseResponse findCourseByCourseName(@RequestBody CourseRequest courseRequest) {
    return courseService.findCourseByCourseName(courseRequest.getCourseName());
  }

  @PostMapping(value = "find_course_by_course_code")
  public CourseResponse findCourseByCourseCode(@RequestBody CourseRequest courseRequest) {
    return courseService.findCourseByCourseCode(courseRequest.getCourseCode());
  }

  @PostMapping(value = "get_selected_courses_by_student_id")
  public CourseResponse getSelectedCoursesByStudentId(@RequestBody CourseRequest courseRequest) {
    return courseService.getSelectedCoursesByStudentId(courseRequest.getStudentId());
  }

  @PostMapping(value = "creat_student")
  public CourseResponse createStudent(@RequestBody CourseRequest courseRequest) {
    return courseService.createStudent(courseRequest.getStudentId(),courseRequest.getStudentName());
  }

}




