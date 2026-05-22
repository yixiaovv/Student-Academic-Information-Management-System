package com.example.yxwbackend.controller;

import com.example.yxwbackend.entity.*;
import com.example.yxwbackend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final StudentRepository studentRepository;
    private final CourseTeacherRepository courseTeacherRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final GradeRepository gradeRepository;

    public AdminController(StudentRepository studentRepository,
                           CourseTeacherRepository courseTeacherRepository,
                           CourseRepository courseRepository,
                           TeacherRepository teacherRepository,
                           GradeRepository gradeRepository) {
        this.studentRepository = studentRepository;
        this.courseTeacherRepository = courseTeacherRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.gradeRepository = gradeRepository;
    }
    
    // 临时接口：一键修复数据问题
    @PostMapping("/fix-data")
    public ResponseEntity<?> fixData() {
        try {
            // 1. 找到王老师（通过工号查找更安全）
            List<Teacher> teachers = teacherRepository.findAll();
            if (teachers.isEmpty()) {
                return ResponseEntity.badRequest().body("没有教师数据！请重启后端");
            }
            Teacher teacher = teachers.get(0);
            
            // 2. 找到所有课程
            List<Course> courses = courseRepository.findAll();
            if (courses.isEmpty()) {
                return ResponseEntity.badRequest().body("没有课程数据！请重启后端");
            }
            
            // 3. 为所有课程添加与王老师的关联
            int addedCount = 0;
            for (Course course : courses) {
                // 检查是否已存在
                boolean exists = courseTeacherRepository.findByCourseId(course.getId())
                        .stream().anyMatch(ct -> ct.getTeacherId().equals(teacher.getId()));
                
                if (!exists) {
                    CourseTeacher ct1 = new CourseTeacher();
                    ct1.setCourseId(course.getId());
                    ct1.setTeacherId(teacher.getId());
                    ct1.setClassName("计算机科学2024-1班");
                    courseTeacherRepository.save(ct1);
                    
                    CourseTeacher ct2 = new CourseTeacher();
                    ct2.setCourseId(course.getId());
                    ct2.setTeacherId(teacher.getId());
                    ct2.setClassName("软件工程2024-1班");
                    courseTeacherRepository.save(ct2);
                    addedCount += 2;
                }
            }
            
            // 4. 更新所有成绩的录入人和状态
            List<Grade> grades = gradeRepository.findAll();
            int updatedCount = 0;
            for (Grade grade : grades) {
                grade.setEnteredBy(teacher.getId());
                grade.setStatus("submitted"); // 直接设为待审核
                gradeRepository.save(grade);
                updatedCount++;
            }
            
            return ResponseEntity.ok("修复成功！添加了 " + addedCount + " 个课程关联，更新了 " + updatedCount + " 条成绩！请刷新页面");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("修复出错：" + e.getMessage());
        }
    }

    @GetMapping("/students")
    public ResponseEntity<List<Map<String, Object>>> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Student s : students) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", s.getId());
            m.put("studentNo", s.getStudentNo());
            m.put("name", s.getName());
            m.put("gender", s.getGender() != null ? s.getGender() : "");
            m.put("className", s.getClassName() != null ? s.getClassName() : "");
            m.put("enrollmentYear", s.getEnrollmentYear());
            result.add(m);
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Студент не найден(学生不存在)"));

        if (body.containsKey("className")) {
            student.setClassName((String) body.get("className"));
        }
        if (body.containsKey("gender")) {
            student.setGender((String) body.get("gender"));
        }
        if (body.containsKey("name")) {
            student.setName((String) body.get("name"));
        }
        if (body.containsKey("enrollmentYear")) {
            student.setEnrollmentYear((Integer) body.get("enrollmentYear"));
        }

        studentRepository.save(student);

        Map<String, Object> result = new HashMap<>();
        result.put("id", student.getId());
        result.put("studentNo", student.getStudentNo());
        result.put("name", student.getName());
        result.put("className", student.getClassName());
        result.put("gender", student.getGender());
        result.put("enrollmentYear", student.getEnrollmentYear());
        return ResponseEntity.ok(result);
    }
}
