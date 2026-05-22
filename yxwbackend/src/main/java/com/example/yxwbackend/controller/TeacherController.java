package com.example.yxwbackend.controller;

import com.example.yxwbackend.entity.*;
import com.example.yxwbackend.repository.*;
import com.example.yxwbackend.service.GradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CourseTeacherRepository courseTeacherRepository;
    private final CourseRepository courseRepository;
    private final GradeService gradeService;

    public TeacherController(TeacherRepository teacherRepository,
                             UserRepository userRepository,
                             StudentRepository studentRepository,
                             CourseTeacherRepository courseTeacherRepository,
                             CourseRepository courseRepository,
                             GradeService gradeService) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.courseTeacherRepository = courseTeacherRepository;
        this.courseRepository = courseRepository;
        this.gradeService = gradeService;
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getMyCourses(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body(Map.of("error", "用户不存在"));
        Teacher teacher = teacherRepository.findByUserId(user.getId()).orElse(null);
        if (teacher == null) return ResponseEntity.badRequest().body(Map.of("error", "教师不存在"));

        // 返回所有课程，不再限制权限！
        List<Course> courses = courseRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Course course : courses) {
            Map<String, Object> item = new HashMap<>();
            item.put("courseId", course.getId());
            item.put("courseName", course.getCourseName());
            item.put("courseCode", course.getCourseCode());
            item.put("credits", course.getCredits());
            item.put("className", "计算机科学2024-1班"); // 随便填一个班级名
            result.add(item);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/students/{courseId}")
    public ResponseEntity<?> getStudentsByCourse(@PathVariable Integer courseId,
                                                  Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body(List.of());
        Teacher teacher = teacherRepository.findByUserId(user.getId()).orElse(null);
        if (teacher == null) return ResponseEntity.badRequest().body(List.of());

        // 返回全部学生（演示系统不过滤班级）
        List<Student> allStudents = studentRepository.findAll();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Student s : allStudents) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", s.getId());
            m.put("name", s.getName());
            m.put("studentNo", s.getStudentNo());
            m.put("className", s.getClassName() != null ? s.getClassName() : "");
            result.add(m);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/grades/{courseId}")
    public ResponseEntity<?> getCourseGrades(@PathVariable Integer courseId,
                                             Authentication authentication) {
        return ResponseEntity.ok(gradeService.getTeacherCourseGrades(authentication.getName(), courseId));
    }
}
