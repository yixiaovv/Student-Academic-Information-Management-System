package com.example.yxwbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "teacher_no", unique = true, nullable = false, length = 20)
    private String teacherNo;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String title;
}
