package com.example.yxwbackend.repository;

import com.example.yxwbackend.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Integer> {
    List<Grade> findByStudentId(Integer studentId);
    List<Grade> findByStudentIdAndStatus(Integer studentId, String status);
    List<Grade> findByCourseId(Integer courseId);
    List<Grade> findByCourseIdAndStatus(Integer courseId, String status);
    List<Grade> findByStatus(String status);
    long countByStudentId(Integer studentId);
}
