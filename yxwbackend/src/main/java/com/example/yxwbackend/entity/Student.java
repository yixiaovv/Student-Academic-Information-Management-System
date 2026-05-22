package com.example.yxwbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "student_no", unique = true, nullable = false, length = 20)
    private String studentNo;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 10)
    private String gender;

    @Column(name = "class_name", length = 50)
    private String className;

    @Column(name = "enrollment_year")
    private Integer enrollmentYear;
}
