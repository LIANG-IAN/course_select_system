package com.example.course_select_system;

import com.example.course_select_system.constants.RtnCode;
import com.example.course_select_system.entity.Course;
import com.example.course_select_system.service.impl.CourseServiceImpl;
import com.example.course_select_system.vo.CourseResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class CourseSelectSystemApplicationTests {
  @Autowired
  CourseServiceImpl courseService;

  //標準的Course
  final Course c1 = new Course("J23", "應用日文", 2, LocalTime.of(15, 0, 0), LocalTime.of(17, 0, 0), 1);

  //課程代碼重複的Course
  final Course c2 = new Course("E11", "應用日文", 2, LocalTime.of(15, 0, 0), LocalTime.of(17, 0, 0), 3);

  //更新課程的Course
  final Course c4 = new Course("J23", "應用日文", 2, LocalTime.of(13, 0, 0), LocalTime.of(15, 0, 0), 3);

  //課程代碼不存在的Course
  final Course c5 = new Course("J10", "應用日文", 2, LocalTime.of(13, 0, 0), LocalTime.of(15, 0, 0), 3);

  //不存在課程代碼
  final String noExistCourse = "J10";

  //有學生選修課程代碼，001選修
  final String enrolledCourse = "E23";

  //有選修課的學生Id，選修E23
  final String enrolledStudentId = "001";

  //標準課程代碼：英文課
  final String standardCourse1 = "E12";

  //標準課程代碼：數學課
  final String standardCourse2 = "M11";

  //標準課程代碼集合：體育課
  List<String> standardCourseList = new ArrayList<>(Arrays.asList("P23"));

  //標準學生Id
  final String standardStudentId = "005";

  //不存在的學生Id
  final String noExistStudentId = "010";

  //新學生Id
  final String newStudentId = "009";

  //新學生姓名
  final String newStudentName = "安妮亞";

  //有選修資料庫沒資料的課程的學生Id
  final String studentEnrolledCourseNoExist = "104";

  @Test
  //增加課程：成功
  public void addCourse1() {
    CourseResponse cr = courseService.addCourse(c1);
    Assert.isTrue(cr.getMessage().equalsIgnoreCase(RtnCode.ADD_COURSE_SUCCESS.getMessage()), "");
  }

  @Test
  //增加課程：課程為null
  public void addCourse2() {
    CourseResponse cr = courseService.addCourse(null);
    Assert.isTrue(cr.getMessage().equals(RtnCode.INCORRECT_COURSE_INFO_ERROR.getMessage()), "");
  }

  @Test
  //增加課程：課程重複
  public void addCourse3() {
    CourseResponse cr = courseService.addCourse(c2);
    Assert.isTrue(cr.getMessage().equals(RtnCode.DUPLICATE_COURSE_ERROR.getMessage()), "");
  }

  @Test
  //更新課程：成功
  public void updateCourse1() {
    CourseResponse cr = courseService.updateCourse(c4);
    Assert.isTrue(cr.getMessage().equals(RtnCode.UPDATE_COURSE_SUCCESS.getMessage()), "");
  }

  @Test
  //更新課程：課程為null
  public void updateCourse2() {
    CourseResponse cr = courseService.updateCourse(null);
    Assert.isTrue(cr.getMessage().equals(RtnCode.INCORRECT_COURSE_INFO_ERROR.getMessage()), "");
  }

  @Test
  //更新課程：課程不存在
  public void updateCourse3() {
    CourseResponse cr = courseService.updateCourse(c5);
    Assert.isTrue(cr.getMessage().equals(RtnCode.COURSE_NOT_FOUND_ERROR.getMessage()), "");
  }

  @Test
  //刪除課程：成功
  public void deleteCourse1() {
    CourseResponse cr = courseService.deleteCourse("J23");
    Assert.isTrue(cr.getMessage().equals(RtnCode.DELETE_COURSE_SUCCESS.getMessage()), "");
  }

  @Test
  //刪除課程：課程為null
  public void deleteCourse2() {
    CourseResponse cr = courseService.deleteCourse(null);
    Assert.isTrue(cr.getMessage().equals(RtnCode.COURSE_CODE_ERROR.getMessage()), "");
  }

  @Test
  //刪除課程：課程不存在
  public void deleteCourse3() {
    CourseResponse cr = courseService.deleteCourse(noExistCourse);
    Assert.isTrue(cr.getMessage().equals(RtnCode.COURSE_NOT_FOUND_ERROR.getMessage()), "");
  }

  @Test
  //刪除課程：尚有學生選修該課程
  public void deleteCourse4() {
    CourseResponse cr = courseService.deleteCourse(enrolledCourse);
    Assert.isTrue(cr.getMessage().equals(RtnCode.CANNOT_DELETE_COURSE_ERROR.getMessage()), "");
  }

  @Test
  //選課：多筆，成功
  public void selectCourse1() {
    List<String> courseCodes = new ArrayList<>();
    courseCodes.add(standardCourse1);
    courseCodes.add(standardCourse2);

    CourseResponse cr = courseService.selectCourse(courseCodes, standardStudentId);
    //Assert.isTrue(cr.getMessage().equals(RtnCode.SELECT_COURSE_SUCCESS.getMessage()), "");
    System.out.println(cr.getMessage());
  }

  @Test
  //選課：課程代碼為空格
  public void selectCourse2() {
    List<String> courseCodes = new ArrayList<>();
    courseCodes.add("");
    CourseResponse cr = courseService.selectCourse(courseCodes, standardStudentId);
    Assert.isTrue(cr.getMessage().equals(RtnCode.COURSE_CODE_ERROR.getMessage()), "");
  }

  @Test
  //選課：學生不存在
  public void selectCourse3() {
    List<String> courseCodes = new ArrayList<>();
    courseCodes.add(standardCourse1);
    CourseResponse cr = courseService.selectCourse(courseCodes, noExistStudentId);
    Assert.isTrue(cr.getMessage().equals(RtnCode.STUDENT_NOT_EXIST_ERROR.getMessage()), "");
  }

  @Test
  //選課：課程代碼不存在
  public void selectCourse4() {
    List<String> courseCodes = new ArrayList<>();
    courseCodes.add(noExistCourse);
    CourseResponse cr = courseService.selectCourse(courseCodes, standardStudentId);
    Assert.isTrue(cr.getMessage().equals(RtnCode.COURSE_NOT_FOUND_ERROR.getMessage()), "");
  }

  @Test
  //選課：課程已滿
  public void selectCourse5() {
    List<String> courseCodes = new ArrayList<>();
    courseCodes.add("M14");
    CourseResponse cr = courseService.selectCourse(courseCodes, standardStudentId);
    Assert.isTrue(cr.getMessage().equals(RtnCode.COURSE_FULL_ERROR.getMessage()), "");
  }

  @Test
  //選課：學生學分超過
  public void selectCourse6() {
    List<String> courseCodes = new ArrayList<>();
    courseCodes.add(standardCourse2);
    CourseResponse cr = courseService.selectCourse(courseCodes, "006");
    Assert.isTrue(cr.getMessage().equals(RtnCode.CREDIT_OVER_LIMIT_ERROR.getMessage()), "");
  }

  @Test
  //選課：學生衝堂
  public void selectCourse7() {
    List<String> courseCodes = new ArrayList<>();
    courseCodes.add("E13");
    CourseResponse cr = courseService.selectCourse(courseCodes, enrolledStudentId);
    Assert.isTrue(cr.getMessage().equals(RtnCode.DUPLICATE_COURSE_TIME_ERROR.getMessage()), "");
  }

  @Test
  //退選：成功
  public void dropCourse1() {
    CourseResponse cr = courseService.dropCourse("P23", "005");
    Assert.isTrue(cr.getMessage().equals(RtnCode.DROP_COURSE_SUCCESS.getMessage()), "");
  }

  @Test
  //退選：課程代碼為null
  public void dropCourse2() {
    CourseResponse cr = courseService.dropCourse(null, "005");
    Assert.isTrue(cr.getMessage().equals(RtnCode.COURSE_CODE_ERROR.getMessage()), "");
  }

  @Test
  //退選：課程不存在
  public void dropCourse3() {
    CourseResponse cr = courseService.dropCourse(noExistCourse, "005");
    Assert.isTrue(cr.getMessage().equals(RtnCode.COURSE_NOT_FOUND_ERROR.getMessage()), "");
  }

  @Test
  //退選：課程不存在
  public void dropCourse4() {
    CourseResponse cr = courseService.dropCourse(standardCourse1, noExistStudentId);
    Assert.isTrue(cr.getMessage().equals(RtnCode.STUDENT_NOT_EXIST_ERROR.getMessage()), "");
  }

  @Test
  //退選：學生未選修該課程
  public void dropCourse5() {
    CourseResponse cr = courseService.dropCourse(standardCourse2, standardStudentId);
    Assert.isTrue(cr.getMessage().equals(RtnCode.NOT_SELECTED_ERROR.getMessage()), "");
  }

  @Test
  //輸出學生選修的所有課程資訊：成功
  public void getSelectedCoursesByStudentId1() {
    CourseResponse cr = courseService.getSelectedCoursesByStudentId(enrolledStudentId);
    Assert.isTrue(cr.getMessage().equals(RtnCode.GET_COURSE_INFO_SUCCESS.getMessage()), "");
  }

  @Test
  //輸出學生選修的所有課程資訊：學生Id為null
  public void getSelectedCoursesByStudentId2() {
    CourseResponse cr = courseService.getSelectedCoursesByStudentId(null);
    Assert.isTrue(cr.getMessage().equals(RtnCode.INPUT_NOT_ALLOWED_BLANK_ERROR.getMessage()), "");
  }

  @Test
  //輸出學生選修的所有課程資訊：學生未選修任何課程
  public void getSelectedCoursesByStudentId3() {
    CourseResponse cr = courseService.getSelectedCoursesByStudentId(standardCourse1);
    Assert.isTrue(cr.getMessage().equals(RtnCode.NOT_SELECTED_ERROR.getMessage()), "");
  }

  @Test
  //輸出學生選修的所有課程資訊：學生選修資料庫不存在的課程
  public void getSelectedCoursesByStudentId4() {
    CourseResponse cr = courseService.getSelectedCoursesByStudentId(studentEnrolledCourseNoExist);
    Assert.isTrue(cr.getMessage().equals(RtnCode.NOT_SELECTED_ERROR.getMessage()), "");
  }

  @Test
  //課程名稱輸出所有同名課程：成功
  public void findCourseByCourseName1() {
    CourseResponse cr = courseService.findCourseByCourseName("國文");
    Assert.isTrue(cr.getMessage().equals(RtnCode.FIND_SUCCESS.getMessage()), "");
  }

  @Test
  //課程名稱輸出所有同名課程：課程名稱為null
  public void findCourseByCourseName2() {
    CourseResponse cr = courseService.findCourseByCourseName(null);
    Assert.isTrue(cr.getMessage().equals(RtnCode.INPUT_EMPTY_VALUE_ERROR.getMessage()), "");
  }

  @Test
  //課程名稱輸出所有同名課程：同名課程不存在
  public void findCourseByCourseName3() {
    CourseResponse cr = courseService.findCourseByCourseName("軍訓");
    Assert.isTrue(cr.getMessage().equals(RtnCode.NO_SAME_COURSE_ERROR.getMessage()), "");
  }

  @Test
  //課程代碼尋找課程：成功
  public void findCourseByCourseCode1() {
    CourseResponse cr = courseService.findCourseByCourseCode(standardCourse2);
    Assert.isTrue(cr.getMessage().equals(RtnCode.FIND_SUCCESS.getMessage()), "");
  }

  @Test
  //課程代碼尋找課程：課程代碼為null
  public void findCourseByCourseCode2() {
    CourseResponse cr = courseService.findCourseByCourseCode(null);
    Assert.isTrue(cr.getMessage().equals(RtnCode.INPUT_EMPTY_VALUE_ERROR.getMessage()), "");
  }

  @Test
  //課程代碼尋找課程：課程代碼符合課程不存在
  public void findCourseByCourseCode3() {
    CourseResponse cr = courseService.findCourseByCourseCode(noExistCourse);
    Assert.isTrue(cr.getMessage().equals(RtnCode.COURSE_NOT_FOUND_ERROR.getMessage()), "");
  }

  @Test
  //創建學生：成功
  public void createStudent1() {
    CourseResponse cr = courseService.createStudent(newStudentId, newStudentName);
    Assert.isTrue(cr.getMessage().equals(RtnCode.ADD_STUDENT_SUCCESS.getMessage()), "");
  }

  @Test
  //創建學生：學生資料為null
  public void createStudent2() {
    CourseResponse cr = courseService.createStudent(newStudentId, null);
    Assert.isTrue(cr.getMessage().equals(RtnCode.INPUT_NOT_ALLOWED_BLANK_ERROR.getMessage()), "");
  }

  @Test
  //創建學生：學生Id重複
  public void createStudent3() {
    CourseResponse cr = courseService.createStudent(standardStudentId, newStudentName);
    Assert.isTrue(cr.getMessage().equals(RtnCode.DUPLICATE_STUDENT_ID_ERROR.getMessage()), "");
  }

  @Test
  //刪除學生：成功
  public void deleteStudent1() {
    CourseResponse cr = courseService.deleteStudent(newStudentId);
    Assert.isTrue(cr.getMessage().equals(RtnCode.DELETE_STUDENT_SUCCESS.getMessage()), "");
  }

  @Test
  //刪除學生：學生Id為null
  public void deleteStudent2() {
    CourseResponse cr = courseService.deleteStudent(null);
    Assert.isTrue(cr.getMessage().equals(RtnCode.INPUT_NOT_ALLOWED_BLANK_ERROR.getMessage()), "");
  }

  @Test
  //刪除學生：學生Id不存在
  public void deleteStudent3() {
    CourseResponse cr = courseService.deleteStudent(noExistStudentId);
    Assert.isTrue(cr.getMessage().equals(RtnCode.STUDENT_NOT_EXIST_ERROR.getMessage()), "");
  }

  @Test
  //刪除學生：學生有選修資料未有資料的課程
  public void deleteStudent4() {
    CourseResponse cr = courseService.deleteStudent(studentEnrolledCourseNoExist);
    Assert.isTrue(cr.getMessage().equals(RtnCode.NO_COURSE_FOUND_ERROR.getMessage()), "");
  }

  //@BeforeEach

}
