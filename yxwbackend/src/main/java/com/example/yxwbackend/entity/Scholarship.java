package com.example.yxwbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "scholarships")
public class Scholarship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id")
    private Integer studentId;

    @Column(length = 50)
    private String type;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 20)
    private String status = "pending";

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt = LocalDateTime.now();
}
