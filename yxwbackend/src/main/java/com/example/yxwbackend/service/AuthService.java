package com.example.yxwbackend.service;

import com.example.yxwbackend.config.JwtUtil;
import com.example.yxwbackend.dto.AuthResponse;
import com.example.yxwbackend.dto.LoginRequest;
import com.example.yxwbackend.dto.RegisterRequest;
import com.example.yxwbackend.entity.Student;
import com.example.yxwbackend.entity.User;
import com.example.yxwbackend.repository.StudentRepository;
import com.example.yxwbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, StudentRepository studentRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName() != null ? request.getDisplayName() : request.getUsername());
        user.setRole("STUDENT");
        userRepository.save(user);

        // 自动创建 Student 记录
        Student student = new Student();
        student.setUserId(user.getId());
        student.setStudentNo(generateStudentNo());
        student.setName(user.getDisplayName());
        student.setGender("");
        student.setClassName("");
        studentRepository.save(student);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getDisplayName(), user.getRole());
    }

    private String generateStudentNo() {
        Optional<String> maxNo = studentRepository.findAll().stream()
                .map(Student::getStudentNo)
                .max(String::compareTo);
        if (maxNo.isPresent()) {
            long next = Long.parseLong(maxNo.get()) + 1;
            return String.valueOf(next);
        }
        return "2025001";
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getDisplayName(), user.getRole());
    }
}
