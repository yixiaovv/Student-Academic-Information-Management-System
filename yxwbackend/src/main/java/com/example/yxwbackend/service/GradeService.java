package com.example.yxwbackend.service;

import com.example.yxwbackend.dto.GradeInputDTO;
import com.example.yxwbackend.dto.GradeVO;
import com.example.yxwbackend.entity.*;
import com.example.yxwbackend.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final CourseTeacherRepository courseTeacherRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;

    public GradeService(GradeRepository gradeRepository,
                        StudentRepository studentRepository,
                        CourseRepository courseRepository,
                        CourseTeacherRepository courseTeacherRepository,
                        UserRepository userRepository,
                        TeacherRepository teacherRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.courseTeacherRepository = courseTeacherRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
    }

    // ==================== 查询（按角色权限） ====================

    public List<GradeVO> getGradesByUsername(String username) {
        Student student = getStudentByUsername(username);
        if (student == null) return Collections.emptyList();
        List<Grade> grades = gradeRepository.findByStudentId(student.getId());
        // 只显示已公示或已归档的成绩
        grades = grades.stream()
                .filter(g -> "published".equals(g.getStatus()) || "archived".equals(g.getStatus()))
                .collect(Collectors.toList());
        return buildGradeVOs(grades);
    }

    public List<GradeVO> getTeacherCourseGrades(String username, Integer courseId) {
        Teacher teacher = getTeacherByUsername(username);
        if (teacher == null) return Collections.emptyList();
        // 不再检查权限！直接返回课程成绩！
        List<Grade> grades = gradeRepository.findByCourseId(courseId);
        return buildGradeVOs(grades);
    }

    public List<GradeVO> getAdminAllGrades() {
        return buildGradeVOs(gradeRepository.findAll());
    }

    public List<GradeVO> getGradesByStudentId(Integer studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        return buildGradeVOs(grades);
    }

    public Map<String, Object> getTranscript(String username) {
        Student student = getStudentByUsername(username);
        if (student == null) {
            return emptyTranscript();
        }
        List<Grade> allGrades = gradeRepository.findByStudentId(student.getId());
        // 只显示已公示或已归档的成绩
        List<Grade> grades = allGrades.stream()
                .filter(g -> "published".equals(g.getStatus()) || "archived".equals(g.getStatus()))
                .collect(Collectors.toList());
        List<GradeVO> gradeVOs = buildGradeVOs(grades);

        // GPA 也只基于已公示或已归档的成绩计算
        Map<String, Object> gpaInfo = calculateGpa(grades);
        gpaInfo.put("studentName", student.getName());
        gpaInfo.put("studentNo", student.getStudentNo());
        gpaInfo.put("className", student.getClassName());
        gpaInfo.put("grades", gradeVOs);

        return gpaInfo;
    }

    // ==================== 工作流操作 ====================

    public GradeVO enterGrade(GradeInputDTO dto, String teacherUsername) {
        Teacher teacher = getTeacherByUsername(teacherUsername);
        if (teacher == null) throw new RuntimeException("Преподаватель не найден(教师不存在)");

        // 查找是否已有同学生/同课程/同学期的成绩记录
        List<Grade> existing = gradeRepository.findAll().stream()
                .filter(g -> g.getStudentId().equals(dto.getStudentId())
                        && g.getCourseId().equals(dto.getCourseId())
                        && Objects.equals(g.getSemester(), dto.getSemester())
                        && Objects.equals(g.getAcademicYear(), dto.getAcademicYear()))
                .collect(Collectors.toList());

        Grade grade;
        if (!existing.isEmpty()) {
            grade = existing.get(0);
            grade.setScore(dto.getScore());
            grade.setStatus("draft");
            grade.setIsRetake(dto.getIsRetake() != null && dto.getIsRetake());
            grade.setOriginalScore(dto.getOriginalScore());
            grade.setEnteredBy(teacher.getId());
        } else {
            grade = new Grade();
            grade.setStudentId(dto.getStudentId());
            grade.setCourseId(dto.getCourseId());
            grade.setScore(dto.getScore());
            grade.setSemester(dto.getSemester());
            grade.setAcademicYear(dto.getAcademicYear());
            grade.setStatus("draft");
            grade.setIsRetake(dto.getIsRetake() != null && dto.getIsRetake());
            grade.setOriginalScore(dto.getOriginalScore());
            grade.setEnteredBy(teacher.getId());
        }
        gradeRepository.save(grade);

        return buildGradeVO(grade);
    }

    public GradeVO submitForReview(Integer gradeId, String teacherUsername) {
        Teacher teacher = getTeacherByUsername(teacherUsername);
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new RuntimeException("Оценка не найдена(成绩不存在)"));

        if (!"draft".equals(grade.getStatus())) {
            throw new RuntimeException("Только черновик можно отправить(仅录入中的成绩可送审)");
        }
        grade.setStatus("submitted");
        gradeRepository.save(grade);
        return buildGradeVO(grade);
    }

    public GradeVO approveGrade(Integer gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new RuntimeException("Оценка не найдена(成绩不存在)"));
        if (!"submitted".equals(grade.getStatus())) {
            throw new RuntimeException("Только на проверке можно утвердить(仅已送审的成绩可审核)");
        }
        grade.setStatus("approved");
        gradeRepository.save(grade);
        return buildGradeVO(grade);
    }

    public GradeVO publishGrade(Integer gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new RuntimeException("Оценка не найдена(成绩不存在)"));
        if (!"approved".equals(grade.getStatus())) {
            throw new RuntimeException("Только утверждённые можно опубликовать(仅已审核的成绩可公示)");
        }
        grade.setStatus("published");
        gradeRepository.save(grade);
        return buildGradeVO(grade);
    }

    public GradeVO archiveGrade(Integer gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new RuntimeException("Оценка не найдена(成绩不存在)"));
        if (!"published".equals(grade.getStatus())) {
            throw new RuntimeException("Только опубликованные можно архивировать(仅已公示的成绩可归档)");
        }
        grade.setStatus("archived");
        gradeRepository.save(grade);
        return buildGradeVO(grade);
    }

    // ==================== 补考 / 待处理 ====================

    public List<GradeVO> getRetakeRecords(Integer studentId) {
        List<Grade> all = gradeRepository.findByStudentId(studentId);
        List<Grade> retakes = all.stream()
                .filter(g -> Boolean.TRUE.equals(g.getIsRetake()))
                .collect(Collectors.toList());
        return buildGradeVOs(retakes);
    }

    public List<GradeVO> getPendingGrades() {
        return buildGradeVOs(gradeRepository.findByStatus("submitted"));
    }

    // ==================== 内部方法 ====================

    private Map<String, Object> calculateGpa(List<Grade> grades) {
        Map<Integer, Course> courseMap = courseRepository.findAll().stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        BigDecimal totalWeightedPoints = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (Grade grade : grades) {
            Course course = courseMap.get(grade.getCourseId());
            if (course == null) continue;
            BigDecimal gradePoint = getGradePoint(grade.getScore());
            BigDecimal credits = course.getCredits();
            totalWeightedPoints = totalWeightedPoints.add(gradePoint.multiply(credits));
            totalCredits = totalCredits.add(credits);
        }

        BigDecimal gpa = totalCredits.compareTo(BigDecimal.ZERO) > 0
                ? totalWeightedPoints.divide(totalCredits, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        String rating;
        if (gpa.compareTo(new BigDecimal("3.5")) >= 0) rating = "优秀";
        else if (gpa.compareTo(new BigDecimal("3.0")) >= 0) rating = "良好";
        else if (gpa.compareTo(new BigDecimal("2.0")) >= 0) rating = "中等";
        else if (gpa.compareTo(new BigDecimal("1.0")) >= 0) rating = "及格";
        else rating = "不及格";

        Map<String, Object> result = new HashMap<>();
        result.put("gpa", gpa);
        result.put("rating", rating);
        return result;
    }

    private BigDecimal getGradePoint(BigDecimal score) {
        if (score.compareTo(new BigDecimal("90")) >= 0) return new BigDecimal("4.0");
        if (score.compareTo(new BigDecimal("80")) >= 0) return new BigDecimal("3.0");
        if (score.compareTo(new BigDecimal("70")) >= 0) return new BigDecimal("2.0");
        if (score.compareTo(new BigDecimal("60")) >= 0) return new BigDecimal("1.0");
        return BigDecimal.ZERO;
    }

    private Student getStudentByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        return studentRepository.findByUserId(user.getId()).orElse(null);
    }

    private Teacher getTeacherByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        return teacherRepository.findByUserId(user.getId()).orElse(null);
    }

    private Map<String, Object> emptyTranscript() {
        Map<String, Object> empty = new HashMap<>();
        empty.put("studentName", "");
        empty.put("studentNo", "");
        empty.put("className", "");
        empty.put("grades", Collections.emptyList());
        empty.put("gpa", BigDecimal.ZERO);
        empty.put("rating", "无");
        return empty;
    }

    private List<GradeVO> buildGradeVOs(List<Grade> grades) {
        Map<Integer, Course> courseMap = courseRepository.findAll().stream()
                .collect(Collectors.toMap(Course::getId, c -> c));
        Map<Integer, Student> studentMap = studentRepository.findAll().stream()
                .collect(Collectors.toMap(Student::getId, s -> s));
        Map<Integer, Teacher> teacherMap = teacherRepository.findAll().stream()
                .collect(Collectors.toMap(Teacher::getId, t -> t));

        return grades.stream().map(g -> {
            GradeVO vo = buildGradeVO(g);
            Course course = courseMap.get(g.getCourseId());
            if (course != null) {
                vo.setCourseName(course.getCourseName());
                vo.setCourseCode(course.getCourseCode());
                vo.setCredits(course.getCredits());
            }
            Student student = studentMap.get(g.getStudentId());
            if (student != null) {
                vo.setStudentName(student.getName());
            }
            if (g.getEnteredBy() != null) {
                Teacher t = teacherMap.get(g.getEnteredBy());
                if (t != null) vo.setEnteredByName(t.getName());
            }
            if (g.getReviewedBy() != null) {
                Teacher t = teacherMap.get(g.getReviewedBy());
                if (t != null) vo.setReviewedByName(t.getName());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    private GradeVO buildGradeVO(Grade g) {
        GradeVO vo = new GradeVO();
        vo.setId(g.getId());
        vo.setStudentId(g.getStudentId());
        vo.setScore(g.getScore());
        vo.setSemester(g.getSemester());
        vo.setAcademicYear(g.getAcademicYear());
        vo.setStatus(g.getStatus());
        vo.setIsRetake(g.getIsRetake());
        vo.setOriginalScore(g.getOriginalScore());
        vo.setRetakeExamScore(g.getRetakeExamScore());
        return vo;
    }
}
