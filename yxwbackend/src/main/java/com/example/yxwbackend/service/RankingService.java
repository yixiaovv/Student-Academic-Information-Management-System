package com.example.yxwbackend.service;

import com.example.yxwbackend.dto.RankingVO;
import com.example.yxwbackend.entity.*;
import com.example.yxwbackend.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankingService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public RankingService(StudentRepository studentRepository,
                          GradeRepository gradeRepository,
                          CourseRepository courseRepository,
                          UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public List<RankingVO> getClassRanking(String className) {
        List<Student> students = studentRepository.findByClassName(className);
        return computeRanking(students);
    }

    public List<RankingVO> getOverallRanking() {
        List<Student> allStudents = studentRepository.findAll();
        return computeRanking(allStudents);
    }

    public RankingVO getMyRanking(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;

        Student student = studentRepository.findByUserId(user.getId()).orElse(null);
        if (student == null || student.getClassName() == null) return null;

        List<Student> classmates = studentRepository.findByClassName(student.getClassName());
        List<RankingVO> rankings = computeRanking(classmates);

        return rankings.stream()
                .filter(r -> r.getStudentNo().equals(student.getStudentNo()))
                .findFirst()
                .orElse(null);
    }

    private List<RankingVO> computeRanking(List<Student> students) {
        Map<Integer, Course> courseMap = courseRepository.findAll().stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        List<StudentGpa> studentGpas = new ArrayList<>();

        for (Student s : students) {
            List<Grade> allGrades = gradeRepository.findByStudentId(s.getId());
            // 只基于已公示或已归档的成绩计算
            List<Grade> grades = allGrades.stream()
                    .filter(g -> "published".equals(g.getStatus()) || "archived".equals(g.getStatus()))
                    .collect(Collectors.toList());
            BigDecimal gpa = calculateGpa(grades, courseMap);
            studentGpas.add(new StudentGpa(s, gpa, getRating(gpa)));
        }

        // 按 GPA 降序排列
        studentGpas.sort((a, b) -> b.gpa.compareTo(a.gpa));

        List<RankingVO> result = new ArrayList<>();
        int rank = 1;
        for (StudentGpa sg : studentGpas) {
            RankingVO vo = new RankingVO();
            vo.setRank(rank++);
            vo.setStudentName(sg.student.getName());
            vo.setStudentNo(sg.student.getStudentNo());
            vo.setClassName(sg.student.getClassName());
            vo.setGpa(sg.gpa);
            vo.setRating(sg.rating);
            vo.setTotalStudents(studentGpas.size());
            result.add(vo);
        }

        return result;
    }

    private BigDecimal calculateGpa(List<Grade> grades, Map<Integer, Course> courseMap) {
        BigDecimal totalWeighted = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (Grade grade : grades) {
            Course course = courseMap.get(grade.getCourseId());
            if (course == null) continue;
            BigDecimal gp = getGradePoint(grade.getScore());
            totalWeighted = totalWeighted.add(gp.multiply(course.getCredits()));
            totalCredits = totalCredits.add(course.getCredits());
        }

        return totalCredits.compareTo(BigDecimal.ZERO) > 0
                ? totalWeighted.divide(totalCredits, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    private BigDecimal getGradePoint(BigDecimal score) {
        if (score.compareTo(new BigDecimal("90")) >= 0) return new BigDecimal("4.0");
        if (score.compareTo(new BigDecimal("80")) >= 0) return new BigDecimal("3.0");
        if (score.compareTo(new BigDecimal("70")) >= 0) return new BigDecimal("2.0");
        if (score.compareTo(new BigDecimal("60")) >= 0) return new BigDecimal("1.0");
        return BigDecimal.ZERO;
    }

    private String getRating(BigDecimal gpa) {
        if (gpa.compareTo(new BigDecimal("3.5")) >= 0) return "优秀";
        if (gpa.compareTo(new BigDecimal("3.0")) >= 0) return "良好";
        if (gpa.compareTo(new BigDecimal("2.0")) >= 0) return "中等";
        if (gpa.compareTo(new BigDecimal("1.0")) >= 0) return "及格";
        return "不及格";
    }

    private static class StudentGpa {
        final Student student;
        final BigDecimal gpa;
        final String rating;

        StudentGpa(Student student, BigDecimal gpa, String rating) {
            this.student = student;
            this.gpa = gpa;
            this.rating = rating;
        }
    }
}
