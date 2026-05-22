package com.example.yxwbackend.controller;

import com.example.yxwbackend.dto.RankingVO;
import com.example.yxwbackend.service.RankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/class/{className}")
    public ResponseEntity<List<RankingVO>> getClassRanking(@PathVariable String className) {
        return ResponseEntity.ok(rankingService.getClassRanking(className));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyRanking(Authentication authentication) {
        RankingVO vo = rankingService.getMyRanking(authentication.getName());
        if (vo == null) {
            return ResponseEntity.ok(Map.of("message", "Нет данных(暂无数据)"));
        }
        return ResponseEntity.ok(vo);
    }

    @GetMapping("/overall")
    public ResponseEntity<List<RankingVO>> getOverallRanking() {
        return ResponseEntity.ok(rankingService.getOverallRanking());
    }
}
