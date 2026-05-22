package com.example.yxwbackend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RankingVO {
    private Integer rank;
    private String studentName;
    private String studentNo;
    private String className;
    private BigDecimal gpa;
    private String rating;
    private Integer totalStudents;
}
