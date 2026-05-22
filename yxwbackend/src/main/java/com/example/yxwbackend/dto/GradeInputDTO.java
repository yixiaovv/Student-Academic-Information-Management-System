package com.example.yxwbackend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GradeInputDTO {
    private Integer studentId;
    private Integer courseId;
    private BigDecimal score;
    private String semester;
    private String academicYear;
    private Boolean isRetake;
    private BigDecimal originalScore;
}
