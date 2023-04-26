package com.example.course_select_system.service.impl;

import com.example.course_select_system.constants.RtnCode;
import com.example.course_select_system.entity.Course;
import com.example.course_select_system.entity.Student;
import com.example.course_select_system.entity.StudentCourseCodes;
import com.example.course_select_system.repository.CourseDao;
import com.example.course_select_system.repository.StudentCourseCodesDao;
import com.example.course_select_system.repository.StudentDao;
import com.example.course_select_system.service.ifs.CourseService;
import com.example.course_select_system.vo.CourseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CourseServiceImpl implements CourseService {

  private final CourseDao courseDao;

  private final StudentDao studentDao;

  private final StudentCourseCodesDao studentCourseCodesDao;

  @Autowired
  public CourseServiceImpl(CourseDao courseDao, StudentDao studentDao, StudentCourseCodesDao studentCourseCodesDao) {
    this.courseDao = courseDao;
    this.studentDao = studentDao;
    this.studentCourseCodesDao = studentCourseCodesDao;
  }

  @Override
  public CourseResponse addCourse(Course course) {
    // 檢查課程資訊是否都符合規範
    if (isInvalidCourse(course)) {
      return new CourseResponse(RtnCode.INCORRECT_COURSE_INFO_ERROR.getMessage());
    }
    // 檢查是否有重複課程
    if (courseDao.existsById(course.getCourseCode())) {
      return new CourseResponse(RtnCode.DUPLICATE_COURSE_ERROR.getMessage());
    }
    // 添加至資料庫
    courseDao.save(course);
    return new CourseResponse(RtnCode.ADD_COURSE_SUCCESS.getMessage());
  }

  @Override
  public CourseResponse updateCourse(Course course) {
    //檢查課程、課程代碼、課程名稱是否為空
    if (course == null || !StringUtils.hasText(course.getCourseCode())) {
      return new CourseResponse(RtnCode.INCORRECT_COURSE_INFO_ERROR.getMessage());
    }
    // 檢查是否有該門課程
    Optional<Course> opCourse = courseDao.findById(course.getCourseCode());
    if (opCourse.isEmpty()) {
      return new CourseResponse(RtnCode.COURSE_NOT_FOUND_ERROR.getMessage());
    }
    Course oldCourse = opCourse.get();
    // 更新資料與資料庫資料完全一樣時，直接跳出
    if (oldCourse.equals(course)) {
      return new CourseResponse(RtnCode.UPDATE_COURSE_SUCCESS.getMessage());
    }
    // 不允許更新課程代碼，那屬於創建課程
    // 更新課程名
    if (StringUtils.hasText(course.getCourseName()) && !course.getCourseName().equals(oldCourse.getCourseName())) {
      oldCourse.setCourseName(course.getCourseName());
    }
    // 更新課程星期
    if (course.getDayOfWeek() >= 1
            || course.getDayOfWeek() <= 5
            && course.getDayOfWeek() != oldCourse.getDayOfWeek()) {
      oldCourse.setDayOfWeek(course.getDayOfWeek());
    }
    // 更新課程開始時間
    if (course.getStartTime().getHour() >= 8
            && course.getStartTime().getHour() <= 17
            && !course.getStartTime().equals(oldCourse.getStartTime())) {
      oldCourse.setStartTime(course.getStartTime());
    }
    // 更新課程結束時間
    if (course.getEndTime().getHour() > oldCourse.getStartTime().getHour()
            && course.getEndTime().getHour() <= 17
            && !course.getEndTime().equals(oldCourse.getEndTime())) {
      oldCourse.setEndTime(course.getEndTime());
    }
    // 更新學分
    if (course.getCredits() >= 1
            && course.getCredits() <= 3
            && course.getCredits() != oldCourse.getCredits()) {
      oldCourse.setCredits(course.getCredits());
    }
    // 修課人數保持原樣不修改
    // 更新課程資訊至資料庫
    courseDao.save(oldCourse);
    return new CourseResponse(oldCourse, RtnCode.UPDATE_COURSE_SUCCESS.getMessage());
  }

  @Override
  public CourseResponse deleteCourse(String courseCode) {
    // 檢查課程代碼是否為空
    if (!StringUtils.hasText(courseCode)) {
      return new CourseResponse(RtnCode.COURSE_CODE_ERROR.getMessage());
    }
    // 檢查課程是否有該課程
    Optional<Course> opCourse = courseDao.findById(courseCode);
    if (opCourse.isEmpty()) {
      return new CourseResponse(RtnCode.COURSE_NOT_FOUND_ERROR.getMessage());
    }
    // 檢查有無學生選修該課程
    if (opCourse.get().getEnrollmentCount() > 0) {
      return new CourseResponse(RtnCode.CANNOT_DELETE_COURSE_ERROR.getMessage());
    }
    // 從資料庫中刪除
    courseDao.deleteById(courseCode);
    return new CourseResponse(RtnCode.DELETE_COURSE_SUCCESS.getMessage());
  }

  @Override
  public CourseResponse selectCourse(List<String> courseCode, String studentId) {
    // 檢查輸入的課程代碼和學生是否為空值
    if (CollectionUtils.isEmpty(courseCode) || !StringUtils.hasText(studentId)) {
      return new CourseResponse(RtnCode.COURSE_CODE_ERROR.getMessage());
    }
    // 檢查學生是否存在
    Optional<Student> opStudent = studentDao.findById(studentId);
    if (opStudent.isEmpty()) {
      return new CourseResponse(RtnCode.STUDENT_NOT_EXIST_ERROR.getMessage());
    }
    Student thisStudent = opStudent.get();
    // 建立回傳專用Student
    for (String s : courseCode) {
      // 檢查輸入的課程名稱
      if (!StringUtils.hasText(s)) {
        return new CourseResponse(RtnCode.INCORRECT_COURSE_NAME_ERROR.getMessage());
      }
      // 搜尋該課程是否存在
      Optional<Course> opCourse = courseDao.findById(s);
      if (opCourse.isEmpty()) {
        return new CourseResponse(RtnCode.COURSE_NOT_FOUND_ERROR.getMessage());
      }
      Course course = opCourse.get();
      // 檢查該課程是否已滿
      if (course.getEnrollmentCount() == 3) {
        return new CourseResponse(RtnCode.COURSE_FULL_ERROR.getMessage());
      }
      // 檢查學生是否學分超過10
      if (thisStudent.getCredit() + course.getCredits() > 10) {
        return new CourseResponse(RtnCode.CREDIT_OVER_LIMIT_COURSE_ERROR.getMessage());
      }
      // 檢查選課是否衝堂
      // 取得學生已選修課程代碼名單
      List<StudentCourseCodes> selectedCourses = studentCourseCodesDao.findCourseCodesByStudentId(studentId);
      if (selectedCourses != null) {
        for (StudentCourseCodes sc : selectedCourses) {
          if (sc != null) {
            String strSelectedCourses = sc.getCourseCode();
            Course c = courseDao.findById(strSelectedCourses).get();
            // 取得已選修的課程星期
            int dayOfWeek = c.getDayOfWeek();
            // 取得欲選修的課程星期
            int courseDayOfWeek = course.getDayOfWeek();
            // 先比較星期，同星期才再比時間
            if (dayOfWeek == courseDayOfWeek) {
              // 取得已選修的課程上下課時間
              LocalTime startTime = c.getStartTime();
              LocalTime endTime = c.getEndTime();
              // 取的欲選修課程上下課時間
              LocalTime courseStartTime = course.getStartTime();
              LocalTime courseEndTime = course.getEndTime();
              if (!(startTime.getHour() >= courseEndTime.getHour())
                      && !(endTime.getHour() <= courseStartTime.getHour())) {
                return new CourseResponse(RtnCode.DUPLICATE_COURSE_TIME_ERROR.getMessage());
              }
            }
          }
        }
      }
      // 計算該堂課程修課人數
      int enrollmentCount = course.getEnrollmentCount() + 1;
      course.setEnrollmentCount(enrollmentCount);
      // 添加選課人數至資料庫
      courseDao.save(course);
      // 儲存學生ID及選課代碼至StudentCourseCodes類型變數裡
      StudentCourseCodes studentCourseCodes = new StudentCourseCodes();
      studentCourseCodes.setStudentId(studentId);
      studentCourseCodes.setCourseCode(course.getCourseCode());
      // 添加學分至選修學生資料庫
      int credit = thisStudent.getCredit() + course.getCredits();
      if (credit > 10) {
        return new CourseResponse(RtnCode.CREDIT_OVER_LIMIT_ERROR.getMessage());
      }
      thisStudent.setCredit(credit);
      // 更新選課代碼至資料庫
      studentCourseCodesDao.save(studentCourseCodes);
      // 更新學生學分數至資料庫
      studentDao.save(thisStudent);
    }
    return new CourseResponse(thisStudent, RtnCode.SELECT_COURSE_SUCCESS.getMessage());
  }

  @Override
  public CourseResponse dropCourse(String courseCode, String studentId) {
    // 檢查輸入的課程代碼和學生是否為空值
    if (!StringUtils.hasText(courseCode) || !StringUtils.hasText(studentId)) {
      return new CourseResponse(RtnCode.COURSE_CODE_ERROR.getMessage());
    }
    // 搜尋該課程是否存在
    Optional<Course> opCourse = courseDao.findById(courseCode);
    if (!opCourse.isPresent()) {
      return new CourseResponse(RtnCode.COURSE_NOT_FOUND_ERROR.getMessage());
    }
    // 搜尋該名學生是否存在
    Optional<Student> opStudent = studentDao.findById(studentId);
    if (!opStudent.isPresent()) {
      return new CourseResponse(RtnCode.STUDENT_NOT_EXIST_ERROR.getMessage());
    }
    Course course = opCourse.get();
    Student student = opStudent.get();
    // 獲得學生修課課程代碼
    StudentCourseCodes s = studentCourseCodesDao.findByStudentIdAndCourseCode(studentId, courseCode);
    if (s == null) {
      return new CourseResponse(RtnCode.NOT_SELECTED_ERROR.getMessage());
    }
    // 刪除學生的修課代碼
    studentCourseCodesDao.delete(s);
    // 刪除學生資料庫中學分
    int credit = student.getCredit() - course.getCredits();
    student.setCredit(credit);
    // 儲存刪減後學生資料
    studentDao.save(student);
    // 計算該堂課程修課人數
    int enrollmentCount = course.getEnrollmentCount() - 1;
    course.setEnrollmentCount(enrollmentCount);
    // 添加選課人數至資料庫
    courseDao.save(course);
    return new CourseResponse(student, RtnCode.DROP_COURSE_SUCCESS.getMessage());
  }

  @Override
  public CourseResponse getSelectedCoursesByStudentId(String studentId) {
    // 檢查學生Id是否為空
    if (!StringUtils.hasText(studentId)) {
      return new CourseResponse(RtnCode.INPUT_NOT_ALLOWED_BLANK_ERROR.getMessage());
    }
    List<StudentCourseCodes> studentCourseCodesList = studentCourseCodesDao.findCourseCodesByStudentId(studentId);
    // 檢查學生是否有選課
    if (CollectionUtils.isEmpty(studentCourseCodesList)) {
      return new CourseResponse(RtnCode.STUDENT_NOT_EXIST_ERROR.getMessage());
    }
    // 透過選課資料庫中的課程代碼去尋找課程資料庫內相符代碼的課程資訊
    // 創建集合紀錄多筆課程
    List<Course> courseList = new ArrayList<>();
    for (StudentCourseCodes s : studentCourseCodesList) {
      Optional<Course> opCourse = courseDao.findById(s.getCourseCode());
      //檢查資料取出值是否為空
      if (!opCourse.isPresent()) {
        return new CourseResponse(RtnCode.NO_COURSE_FOUND_ERROR.getMessage());
      }
      courseList.add(opCourse.get());
    }
    return new CourseResponse(courseList, RtnCode.GET_COURSE_INFO_SUCCESS.getMessage());
  }

  @Override
  public CourseResponse findCourseByCourseName(String courseName) {
    // 檢查課程名稱是否為空
    if (!StringUtils.hasText(courseName)) {
      return new CourseResponse(RtnCode.INPUT_EMPTY_VALUE_ERROR.getMessage());
    }
    // 取得所有同名稱課程
    List<Course> courseList = courseDao.findCourseByCourseName(courseName);
    // 檢查是否有相同名稱課程存在
    if (CollectionUtils.isEmpty(courseList)) {
      return new CourseResponse(RtnCode.NO_SAME_COURSE_ERROR.getMessage());
    }
    return new CourseResponse(courseList, RtnCode.FIND_SUCCESS.getMessage());
  }

  @Override
  public CourseResponse findCourseByCourseCode(String courseCode) {
    // 檢查課程代碼是否為空
    if (!StringUtils.hasText(courseCode)) {
      return new CourseResponse(RtnCode.INPUT_EMPTY_VALUE_ERROR.getMessage());
    }
    // 檢查是否有相同名稱代碼存在
    Optional<Course> opCourse = courseDao.findById(courseCode);
    if (!opCourse.isPresent()) {
      return new CourseResponse(RtnCode.COURSE_NOT_FOUND_ERROR.getMessage());
    }
    Course course = opCourse.get();
    return new CourseResponse(course, RtnCode.FIND_SUCCESS.getCode());
  }

  @Override
  public CourseResponse createStudent(String studentId, String studentName) {
    // 檢查學生Id、學生姓名是否為空
    if (!StringUtils.hasText(studentId) || !StringUtils.hasText(studentName)) {
      return new CourseResponse(RtnCode.INPUT_NOT_ALLOWED_BLANK_ERROR.getMessage());
    }
    // 檢查是否有重複學生ID
    if (studentDao.existsById(studentId)) {
      return new CourseResponse(RtnCode.DUPLICATE_STUDENT_ID_ERROR.getMessage());
    }
    // 將ID與姓名打包成Student類型資料
    Student student = new Student();
    student.setStudentId(studentId);
    student.setName(studentName);
    // 創建新學生至資料庫
    studentDao.save(student);
    return new CourseResponse(student, RtnCode.ADD_STUDENT_SUCCESS.getMessage());
  }

  @Override
  public CourseResponse deleteStudent(String studentId) {
    // 檢查學生Id是否為空
    if (studentId == null) {
      return new CourseResponse(RtnCode.INPUT_NOT_ALLOWED_BLANK_ERROR.getMessage());
    }
    // 先檢查學生是否存在
    if (studentDao.existsById(studentId)) {
      return new CourseResponse(RtnCode.STUDENT_NOT_EXIST_ERROR.getMessage());
    }
    //取得學生選課課程資料
    List<StudentCourseCodes> studentCourseCodesList = studentCourseCodesDao.findCourseCodesByStudentId(studentId);
    //因有多筆課程代碼，存在List裡，下面批次從課程資料庫裡刪除
    for (StudentCourseCodes s : studentCourseCodesList) {
      String courseCode = s.getCourseCode();
      if (StringUtils.hasText(courseCode)) {
        Optional<Course> opCourse = courseDao.findById(courseCode);
        if (!opCourse.isPresent()) {
          return new CourseResponse(RtnCode.NO_COURSE_FOUND_ERROR.getMessage());
        }
        Course course = opCourse.get();
        //課程退選
        int enrollment_count = course.getEnrollmentCount();
        enrollment_count--;
        course.setEnrollmentCount(enrollment_count);
        //更新課程
        courseDao.save(course);
        // 將學生從選課資料庫中刪除
        studentCourseCodesDao.delete(s);
      }
    }
    // 將學生從學生資料庫中刪除
    studentDao.deleteById(studentId);
    return new CourseResponse(RtnCode.DELETE_STUDENT_SUCCESS.getMessage());
  }

  private boolean isInvalidCourse(Course course) {
    // 檢查課程代碼是否為空
    return !StringUtils.hasText(course.getCourseCode())
            // 檢查課程名稱是否為空
            || !StringUtils.hasText(course.getCourseName())
            // 檢查課程星期是否為一到五
            || course.getDayOfWeek() <= 1 || course.getDayOfWeek() >= 5
            // 檢查課程開始時間
            || course.getStartTime().getHour() >= 8
            && course.getStartTime().getHour() <= 17
            // 檢查課程結束時間
            || course.getEndTime().getHour() > course.getStartTime().getHour()
            && course.getEndTime().getHour() <= 17
            // 檢查學分是否1~3
            || course.getCredits() >= 1 || course.getCredits() <= 3;
  }
}
