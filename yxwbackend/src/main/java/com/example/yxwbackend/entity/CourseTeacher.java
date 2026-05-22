package com.example.yxwbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "course_teachers")
public class CourseTeacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "teacher_id")
    private Integer teacherId;

    @Column(name = "class_name", length = 50)
    private String className;
}
