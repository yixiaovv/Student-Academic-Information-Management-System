package com.example.yxwbackend;

import com.example.yxwbackend.entity.*;
import com.example.yxwbackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final CourseTeacherRepository courseTeacherRepository;
    private final GradeRepository gradeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           StudentRepository studentRepository,
                           TeacherRepository teacherRepository,
                           CourseRepository courseRepository,
                           CourseTeacherRepository courseTeacherRepository,
                           GradeRepository gradeRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.courseTeacherRepository = courseTeacherRepository;
        this.gradeRepository = gradeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 清空所有现有数据，强制重新初始化
        gradeRepository.deleteAll();
        courseTeacherRepository.deleteAll();
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
        userRepository.deleteAll();

        // ===== 用户 =====
        User u1 = createUser("zhangsan", "123456", "张三", "STUDENT");
        User u2 = createUser("lisi", "123456", "李四", "STUDENT");
        User u3 = createUser("admin", "123456", "管理员", "ADMIN");
        User u4 = createUser("wanglaoshi", "123456", "王老师", "TEACHER");

        // ===== 学生 =====
        Student s1 = createStudent(u1.getId(), "2024001", "张三", "男", "计算机科学2024-1班", 2024);
        Student s2 = createStudent(u2.getId(), "2024002", "李四", "女", "软件工程2024-1班", 2024);
        Student s3 = createStudent(u3.getId(), "2024003", "管理员", "男", "计算机科学2024-1班", 2024);

        // ===== 教师 =====
        Teacher t1 = createTeacher(u4.getId(), "T2024001", "王老师", "副教授");

        // ===== 课程 =====
        Course c1 = createCourse("CS101", "高等数学", 5.0);
        Course c2 = createCourse("CS102", "程序设计基础", 4.0);
        Course c3 = createCourse("CS103", "数据结构", 3.0);
        Course c4 = createCourse("CS104", "数据库原理", 3.5);

        // ===== 课程-教师关联 =====
        createCourseTeacher(c1.getId(), t1.getId(), "计算机科学2024-1班");
        createCourseTeacher(c1.getId(), t1.getId(), "软件工程2024-1班");
        createCourseTeacher(c2.getId(), t1.getId(), "计算机科学2024-1班");
        createCourseTeacher(c3.getId(), t1.getId(), "计算机科学2024-1班");
        createCourseTeacher(c3.getId(), t1.getId(), "软件工程2024-1班");
        createCourseTeacher(c4.getId(), t1.getId(), "计算机科学2024-1班");
        createCourseTeacher(c4.getId(), t1.getId(), "软件工程2024-1班");

        // ===== 成绩（状态 submitted 方便管理员直接看到待审核） =====
        createGrade(s1.getId(), c1.getId(), 92, "第一学期", "2024-2025", "submitted", false, null, t1.getId());
        createGrade(s1.getId(), c2.getId(), 85, "第一学期", "2024-2025", "submitted", false, null, t1.getId());
        createGrade(s1.getId(), c3.getId(), 88, "第二学期", "2024-2025", "submitted", false, null, t1.getId());
        createGrade(s1.getId(), c4.getId(), 76, "第二学期", "2024-2025", "submitted", false, null, t1.getId());
        createGrade(s2.getId(), c1.getId(), 78, "第一学期", "2024-2025", "submitted", false, null, t1.getId());
        createGrade(s2.getId(), c2.getId(), 95, "第一学期", "2024-2025", "submitted", false, null, t1.getId());
        createGrade(s2.getId(), c3.getId(), 82, "第二学期", "2024-2025", "submitted", false, null, t1.getId());
        createGrade(s2.getId(), c4.getId(), 90, "第二学期", "2024-2025", "submitted", false, null, t1.getId());
        createGrade(s3.getId(), c1.getId(), 91, "第一学期", "2024-2025", "submitted", false, null, t1.getId());
        createGrade(s3.getId(), c2.getId(), 87, "第一学期", "2024-2025", "submitted", false, null, t1.getId());
        createGrade(s3.getId(), c3.getId(), 93, "第二学期", "2024-2025", "submitted", false, null, t1.getId());
        createGrade(s3.getId(), c4.getId(), 89, "第二学期", "2024-2025", "submitted", false, null, t1.getId());
    }

    private User createUser(String username, String password, String displayName, String role) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        u.setDisplayName(displayName);
        u.setRole(role);
        return userRepository.save(u);
    }

    private Student createStudent(Integer userId, String no, String name, String gender, String className, int year) {
        Student s = new Student();
        s.setUserId(userId);
        s.setStudentNo(no);
        s.setName(name);
        s.setGender(gender);
        s.setClassName(className);
        s.setEnrollmentYear(year);
        return studentRepository.save(s);
    }

    private Teacher createTeacher(Integer userId, String no, String name, String title) {
        Teacher t = new Teacher();
        t.setUserId(userId);
        t.setTeacherNo(no);
        t.setName(name);
        t.setTitle(title);
        return teacherRepository.save(t);
    }

    private Course createCourse(String code, String name, double credits) {
        Course c = new Course();
        c.setCourseCode(code);
        c.setCourseName(name);
        c.setCredits(BigDecimal.valueOf(credits));
        return courseRepository.save(c);
    }

    private void createCourseTeacher(Integer courseId, Integer teacherId, String className) {
        CourseTeacher ct = new CourseTeacher();
        ct.setCourseId(courseId);
        ct.setTeacherId(teacherId);
        ct.setClassName(className);
        courseTeacherRepository.save(ct);
    }

    private void createGrade(Integer studentId, Integer courseId, double score,
                             String semester, String year, String status,
                             boolean isRetake, BigDecimal originalScore, Integer enteredBy) {
        Grade g = new Grade();
        g.setStudentId(studentId);
        g.setCourseId(courseId);
        g.setScore(BigDecimal.valueOf(score));
        g.setSemester(semester);
        g.setAcademicYear(year);
        g.setStatus(status);
        g.setIsRetake(isRetake);
        g.setOriginalScore(originalScore);
        g.setEnteredBy(enteredBy);
        gradeRepository.save(g);
    }
}
