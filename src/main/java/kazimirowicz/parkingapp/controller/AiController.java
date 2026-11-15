package kazimirowicz.parkingapp.controller;

import jakarta.validation.Valid;
import kazimirowicz.parkingapp.dto.request.AiRecommendationRequest;
import kazimirowicz.parkingapp.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/recommend")
    public ResponseEntity<Map<String, String>> getRecommendation(@Valid @RequestBody AiRecommendationRequest request) {
        String recommendation = aiService.getParkingRecommendation(request.query());
        return ResponseEntity.ok(Map.of("recommendation", recommendation));
    }
}
