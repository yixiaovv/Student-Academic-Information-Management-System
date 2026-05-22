package com.example.yxwbackend.repository;

import com.example.yxwbackend.entity.CourseTeacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseTeacherRepository extends JpaRepository<CourseTeacher, Integer> {
    List<CourseTeacher> findByTeacherId(Integer teacherId);
    List<CourseTeacher> findByCourseId(Integer courseId);
    List<CourseTeacher> findByTeacherIdAndClassName(Integer teacherId, String className);
}
