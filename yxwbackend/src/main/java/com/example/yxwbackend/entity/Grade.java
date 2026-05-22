package com.example.yxwbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "grades", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id", "semester", "academic_year"})
})
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "course_id")
    private Integer courseId;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(length = 20)
    private String semester;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Column(length = 20)
    private String status = "draft";

    @Column(name = "is_retake")
    private Boolean isRetake = false;

    @Column(name = "original_score", precision = 5, scale = 2)
    private BigDecimal originalScore;

    @Column(name = "retake_exam_score", precision = 5, scale = 2)
    private BigDecimal retakeExamScore;

    @Column(name = "entered_by")
    private Integer enteredBy;

    @Column(name = "reviewed_by")
    private Integer reviewedBy;
}
