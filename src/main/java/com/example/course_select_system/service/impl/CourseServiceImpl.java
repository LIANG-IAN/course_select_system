package com.example.course_select_system.service.impl;

import com.example.course_select_system.constants.ConstantData;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.util.*;


@Service
@Transactional
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
    if (course == null) {
      return new CourseResponse(RtnCode.INCORRECT_COURSE_INFO_ERROR.getMessage());
    }
    // 檢查是否有重複課程
    if (courseDao.existsById(course.getCourseCode())) {
      return new CourseResponse(RtnCode.DUPLICATE_COURSE_ERROR.getMessage());
    }
    // 檢查課程資訊是否符合規定
    if (!isInvalidCourse(course)) {
      return new CourseResponse(RtnCode.INCORRECT_COURSE_INFO_ERROR.getMessage());
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
    if (course.getDayOfWeek() >= ConstantData.DAY_OF_WEEK_START
            || course.getDayOfWeek() <= ConstantData.DAY_OF_WEEK_END
            && course.getDayOfWeek() != oldCourse.getDayOfWeek()) {
      oldCourse.setDayOfWeek(course.getDayOfWeek());
    }
    // 更新課程開始時間
    if (course.getStartTime().getHour() >= ConstantData.START_TIME_START
            && course.getStartTime().getHour() <= ConstantData.START_TIME_END

            && course.getStartTime().getHour() < course.getEndTime().getHour()
            && !course.getStartTime().equals(oldCourse.getStartTime())
            && course.getStartTime().getHour() != course.getEndTime().getHour()) {
      oldCourse.setStartTime(course.getStartTime());
    }
    // 更新課程結束時間
    if (course.getEndTime().getHour() > oldCourse.getStartTime().getHour()
            && course.getEndTime().getHour() <= ConstantData.START_TIME_END
            && !course.getEndTime().equals(oldCourse.getEndTime())
            && course.getStartTime().getHour() != course.getEndTime().getHour()) {
      oldCourse.setEndTime(course.getEndTime());
    }
    // 更新學分
    if (course.getCredits() >= ConstantData.COURSE_CREDITS_LEST
            && course.getCredits() <= ConstantData.COURSE_CREDITS_MAX
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
    // 檢查courseCode內是否有重複課程代碼
    Set<String> courseCodeSet = new HashSet<>(courseCode);
    Student thisStudent = opStudent.get();
    for (String s : courseCodeSet) {
      // 檢查輸入的課程名稱
      if (!StringUtils.hasText(s)) {
        // 檢查輸入的課程代碼是否為空
        return new CourseResponse(RtnCode.COURSE_CODE_ERROR.getMessage());
      }
      // 搜尋該課程是否存在
      Optional<Course> opCourse = courseDao.findById(s);
      if (opCourse.isEmpty()) {
        return new CourseResponse(RtnCode.COURSE_NOT_FOUND_ERROR.getMessage());
      }
      Course course = opCourse.get();
      // 檢查該課程是否已滿
      if (course.getEnrollmentCount() == ConstantData.ENROLLMENT) {
        return new CourseResponse(RtnCode.COURSE_FULL_ERROR.getMessage());
      }
      // 檢查學生是否學分超過10
      int credit = thisStudent.getCredit() + course.getCredits();
      if (credit > ConstantData.STUDENT_CREDITS) {
        return new CourseResponse(RtnCode.CREDIT_OVER_LIMIT_ERROR.getMessage());
      }
      // 檢查選課是否衝堂
      // 取得學生已選修課程代碼名單
      List<StudentCourseCodes> selectedCourses = studentCourseCodesDao.findCourseCodesByStudentId(studentId);
      // 課程代碼為空代表無選修任何課程，直接跳過for迴圈
      if (!CollectionUtils.isEmpty(selectedCourses)) {
        for (StudentCourseCodes sc : selectedCourses) {
          // 檢查課程代碼不為空
          if (sc != null) {
            String strSelectedCourses = sc.getCourseCode();
            //檢查資料庫取出的資料
            Optional<Course> optionalCourse = courseDao.findById(strSelectedCourses);
            // 資料庫防呆
            if (optionalCourse.isEmpty()) {
              return new CourseResponse(RtnCode.COURSE_NOT_FOUND_ERROR.getMessage());
            }
            // 通過撈資料庫與下方衝堂判斷，可以間接判斷學生欲選課程是否跟學生已選課程重複
            Course c = optionalCourse.get();
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
              // 檢查上下課時間是否與選修中課程衝堂
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
      StudentCourseCodes studentCourseCodes = new StudentCourseCodes(studentId,course.getCourseCode());
      // 添加學分至選修學生資料庫
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
    // 檢查是否有選修該堂課
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
      return new CourseResponse(RtnCode.NOT_SELECTED_ERROR.getMessage());
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
      return new CourseResponse(RtnCode.COURSE_NOT_FOUND_ERROR.getMessage());
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
    return new CourseResponse(opCourse.get(), RtnCode.FIND_SUCCESS.getCode());
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
    Student student = new Student(studentId, studentName);
    // 創建新學生至資料庫
    studentDao.save(student);
    return new CourseResponse(student, RtnCode.ADD_STUDENT_SUCCESS.getMessage());
  }

  @Override
  public CourseResponse deleteStudent(String studentId) {
    // 檢查學生Id是否為空
    if (!StringUtils.hasText(studentId)) {
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
        // 檢查是否有選修該堂課程
        if (!opCourse.isPresent()) {
          return new CourseResponse(RtnCode.NO_COURSE_FOUND_ERROR.getMessage());
        }
        Course course = opCourse.get();
        //課程退選
        int enrollmentCount = course.getEnrollmentCount();
        enrollmentCount--;
        course.setEnrollmentCount(enrollmentCount);
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
    return StringUtils.hasText(course.getCourseCode())
            // 檢查課程名稱是否為空
            || StringUtils.hasText(course.getCourseName())
            // 檢查課程星期是否為一到五
            || course.getDayOfWeek() >= ConstantData.DAY_OF_WEEK_START || course.getDayOfWeek() <= ConstantData.DAY_OF_WEEK_END
            // 檢查課程開始時間
            || course.getStartTime().getHour() >= ConstantData.START_TIME_START
            && course.getStartTime().getHour() <= ConstantData.START_TIME_END
            // 檢查課程結束時間
            || course.getEndTime().getHour() > course.getStartTime().getHour()
            && course.getEndTime().getHour() <= ConstantData.START_TIME_END
            // 檢查學分是否1~3
            || course.getCredits() >= ConstantData.COURSE_CREDITS_LEST || course.getCredits() <= ConstantData.COURSE_CREDITS_MAX;
  }
}
