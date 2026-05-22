package com.example.yxwbackend.dto;

import lombok.Data;

@Data
public class GradeWorkflowDTO {
    private Integer gradeId;
    private String action; // submit, approve, publish, archive
}
