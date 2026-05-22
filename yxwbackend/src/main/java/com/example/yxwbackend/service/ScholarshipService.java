package com.example.yxwbackend.service;

import com.example.yxwbackend.dto.ScholarshipVO;
import com.example.yxwbackend.entity.*;
import com.example.yxwbackend.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScholarshipService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ScholarshipRepository scholarshipRepository;

    public ScholarshipService(GradeRepository gradeRepository,
                              StudentRepository studentRepository,
                              CourseRepository courseRepository,
                              UserRepository userRepository,
                              ScholarshipRepository scholarshipRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.scholarshipRepository = scholarshipRepository;
    }

    public ScholarshipVO calculateForStudent(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        List<Grade> allGrades = gradeRepository.findByStudentId(studentId);
        // 只基于已公示或已归档的成绩计算
        List<Grade> grades = allGrades.stream()
                .filter(g -> "published".equals(g.getStatus()) || "archived".equals(g.getStatus()))
                .collect(Collectors.toList());
        Map<String, Object> gpaInfo = calculateGpa(grades);
        BigDecimal gpa = (BigDecimal) gpaInfo.get("gpa");
        String rating = (String) gpaInfo.get("rating");

        String type;
        BigDecimal amount;

        if (gpa.compareTo(new BigDecimal("3.8")) >= 0) {
            type = "一等奖学金";
            amount = new BigDecimal("5000");
        } else if (gpa.compareTo(new BigDecimal("3.5")) >= 0) {
            type = "二等奖学金";
            amount = new BigDecimal("3000");
        } else if (gpa.compareTo(new BigDecimal("3.0")) >= 0) {
            type = "三等奖学金";
            amount = new BigDecimal("1000");
        } else {
            type = "无";
            amount = BigDecimal.ZERO;
        }

        // 保存计算结果
        Scholarship scholarship = new Scholarship();
        scholarship.setStudentId(studentId);
        scholarship.setType(type);
        scholarship.setAmount(amount);
        scholarship.setStatus("已计算");
        scholarship.setCalculatedAt(LocalDateTime.now());
        scholarshipRepository.save(scholarship);

        // 构建返回
        ScholarshipVO vo = new ScholarshipVO();
        vo.setId(scholarship.getId());
        vo.setStudentName(student.getName());
        vo.setStudentNo(student.getStudentNo());
        vo.setType(type);
        vo.setAmount(amount);
        vo.setStatus(scholarship.getStatus());
        vo.setCalculatedAt(scholarship.getCalculatedAt());
        vo.setGpa(gpa);
        vo.setRating(rating);

        return vo;
    }

    public ScholarshipVO getMyScholarship(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            ScholarshipVO empty = new ScholarshipVO();
            empty.setType("无");
            empty.setAmount(BigDecimal.ZERO);
            empty.setStatus("无数据");
            return empty;
        }
        Student student = studentRepository.findByUserId(user.getId()).orElse(null);
        if (student == null) {
            ScholarshipVO empty = new ScholarshipVO();
            empty.setType("无");
            empty.setAmount(BigDecimal.ZERO);
            empty.setStatus("无数据");
            return empty;
        }

        Optional<Scholarship> latest = scholarshipRepository
                .findTopByStudentIdOrderByCalculatedAtDesc(student.getId());

        if (latest.isPresent()) {
            Scholarship s = latest.get();
            ScholarshipVO vo = new ScholarshipVO();
            vo.setId(s.getId());
            vo.setStudentName(student.getName());
            vo.setStudentNo(student.getStudentNo());
            vo.setType(s.getType());
            vo.setAmount(s.getAmount());
            vo.setStatus(s.getStatus());
            vo.setCalculatedAt(s.getCalculatedAt());

            // 重新计算 GPA 和评级（只基于已公示或已归档的成绩）
            List<Grade> allGrades = gradeRepository.findByStudentId(student.getId());
            List<Grade> grades = allGrades.stream()
                    .filter(g -> "published".equals(g.getStatus()) || "archived".equals(g.getStatus()))
                    .collect(Collectors.toList());
            Map<String, Object> gpaInfo = calculateGpa(grades);
            vo.setGpa((BigDecimal) gpaInfo.get("gpa"));
            vo.setRating((String) gpaInfo.get("rating"));

            return vo;
        }

        // 如果还没有计算过，自动计算
        return calculateForStudent(student.getId());
    }

    private Map<String, Object> calculateGpa(List<Grade> grades) {
        Map<Integer, Course> courseMap = courseRepository.findAll().stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        BigDecimal totalWeightedPoints = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (Grade grade : grades) {
            Course course = courseMap.get(grade.getCourseId());
            if (course == null) continue;

            BigDecimal gradePoint = getGradePoint(grade.getScore());
            totalWeightedPoints = totalWeightedPoints.add(gradePoint.multiply(course.getCredits()));
            totalCredits = totalCredits.add(course.getCredits());
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
}
