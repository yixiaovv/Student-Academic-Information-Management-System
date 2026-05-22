package com.example.yxwbackend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GradeVO {
    private Integer id;
    private Integer studentId;
    private String studentName;
    private String courseName;
    private String courseCode;
    private BigDecimal credits;
    private BigDecimal score;
    private String semester;
    private String academicYear;
    private String status;
    private Boolean isRetake;
    private BigDecimal originalScore;
    private BigDecimal retakeExamScore;
    private String enteredByName;
    private String reviewedByName;
}
