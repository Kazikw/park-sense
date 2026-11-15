package kazimirowicz.parkingapp.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AiRecommendationRequest(
        @NotBlank(message = "Zapytanie do asystenta AI nie może być puste")
        String query
) {
}
