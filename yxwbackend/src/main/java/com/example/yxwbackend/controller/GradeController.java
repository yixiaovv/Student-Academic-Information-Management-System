package com.example.yxwbackend.controller;

import com.example.yxwbackend.dto.GradeInputDTO;
import com.example.yxwbackend.dto.GradeVO;
import com.example.yxwbackend.dto.GradeWorkflowDTO;
import com.example.yxwbackend.service.GradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping
    public ResponseEntity<?> getMyGrades(Authentication authentication) {
        return ResponseEntity.ok(gradeService.getGradesByUsername(authentication.getName()));
    }

    @GetMapping("/transcript")
    public ResponseEntity<?> getTranscript(Authentication authentication) {
        return ResponseEntity.ok(gradeService.getTranscript(authentication.getName()));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getGradesByStudentId(@PathVariable Integer studentId) {
        return ResponseEntity.ok(gradeService.getGradesByStudentId(studentId));
    }

    // ==================== 教师：成绩录入 ====================

    @PostMapping("/enter")
    public ResponseEntity<?> enterGrade(@RequestBody GradeInputDTO dto,
                                        Authentication authentication) {
        try {
            return ResponseEntity.ok(gradeService.enterGrade(dto, authentication.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== 教师/管理员：工作流 ====================

    @PostMapping("/workflow")
    public ResponseEntity<?> workflow(@RequestBody GradeWorkflowDTO dto,
                                      Authentication authentication) {
        try {
            String role = authentication.getAuthorities().stream()
                    .findFirst().map(a -> a.getAuthority()).orElse("");
            GradeVO result = null;
            switch (dto.getAction()) {
                case "submit":
                    result = gradeService.submitForReview(dto.getGradeId(), authentication.getName());
                    break;
                case "approve":
                    if (!role.contains("ADMIN")) throw new RuntimeException("Доступ запрещён(权限不足)");
                    result = gradeService.approveGrade(dto.getGradeId());
                    break;
                case "publish":
                    if (!role.contains("ADMIN")) throw new RuntimeException("Доступ запрещён(权限不足)");
                    result = gradeService.publishGrade(dto.getGradeId());
                    break;
                case "archive":
                    if (!role.contains("ADMIN")) throw new RuntimeException("Доступ запрещён(权限不足)");
                    result = gradeService.archiveGrade(dto.getGradeId());
                    break;
                default:
                    return ResponseEntity.badRequest().body(Map.of("error", "Неизвестное действие(未知操作)"));
            }
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== 待处理成绩（管理员审核用） ====================

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingGrades() {
        // 返回所有成绩（除了已归档的），让管理员可以一步步操作
        return ResponseEntity.ok(gradeService.getAdminAllGrades());
    }

    // ==================== 补考记录 ====================

    @GetMapping("/retake/{studentId}")
    public ResponseEntity<?> getRetakeRecords(@PathVariable Integer studentId) {
        return ResponseEntity.ok(gradeService.getRetakeRecords(studentId));
    }
}
