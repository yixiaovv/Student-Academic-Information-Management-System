package com.example.yxwbackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScholarshipVO {
    private Integer id;
    private String studentName;
    private String studentNo;
    private String type;
    private BigDecimal amount;
    private String status;
    private LocalDateTime calculatedAt;

    private BigDecimal gpa;
    private String rating;
}
