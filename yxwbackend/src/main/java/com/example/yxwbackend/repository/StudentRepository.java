package com.example.yxwbackend.repository;

import com.example.yxwbackend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByUserId(Integer userId);
    Optional<Student> findByStudentNo(String studentNo);
    List<Student> findByClassName(String className);
}
