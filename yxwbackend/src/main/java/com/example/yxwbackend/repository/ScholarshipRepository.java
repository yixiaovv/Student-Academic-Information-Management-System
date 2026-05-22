package com.example.yxwbackend.repository;

import com.example.yxwbackend.entity.Scholarship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScholarshipRepository extends JpaRepository<Scholarship, Integer> {
    Optional<Scholarship> findTopByStudentIdOrderByCalculatedAtDesc(Integer studentId);
    List<Scholarship> findByStudentId(Integer studentId);
}
