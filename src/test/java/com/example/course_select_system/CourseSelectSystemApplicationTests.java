package com.example.course_select_system;

import com.example.course_select_system.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CourseSelectSystemApplicationTests {
  @Autowired
  CourseServiceImpl courseService;



  @BeforeAll
  private void beforeAll() {

  }

  @BeforeEach
  private void beforeEach() {

  }

  @AfterEach
  private void afterEach() {

  }

  @AfterAll
  private void afterAll() {

  }

}
