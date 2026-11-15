package kazimirowicz.parkingapp.service;

import kazimirowicz.parkingapp.dto.response.ParkingZoneResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private static final String GEMINI_MODEL = "gemini-3.5-flash";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent";

    private final ParkingZoneService parkingZoneService;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${gemini.api-key:${GEMINI_API_KEY:brak_klucza}}")
    private String apiKey;

    public String getParkingRecommendation(String userQuery) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Brak klucza API Gemini (tryb Safe Mode). Zwracanie zmockowanej rekomendacji dla zapytania: {}", userQuery);
            return "🤖 [Mock AI]: Z analizy wynika, że P&R Lipowa ma najwyższą szansę na wolne miejsce (~86%). Galeria Centrum jest niemal pełna, a przy Dworcu Głównym szanse są umiarkowane — śmiało kieruj się na Lipową!";
        }
        List<ParkingZoneResponse> zones = parkingZoneService.getAllZones();
        String zonesSummary = formatZonesForPrompt(zones);

        String systemPrompt = """
                Jesteś inteligentnym, lokalnym doradcą parkingowym Park-Sense.
                Oto aktualne dane na żywo ze stref parkingowych w mieście:
                %s

                Kontekst lokalizacyjny i komunikacyjny stref (uwzględnij przy analizie trasy i odległości na mapie):
                - 'Galeria Centrum' oraz 'Stare Miasto - Podwale': ścisłe centrum miasta, bezpośrednie sąsiedztwo rynku, restauracji i sklepów, ale duży ruch i trudny wjazd w godzinach szczytu.
                - 'Dworzec Główny': węzeł przesiadkowy blisko centrum (ok. 10 min pieszo od rynku, 3 min tramwajem).
                - 'Kampus Politechniki': dzielnica akademicko-biznesowa, ok. 15 min spacerem lub 5 min autobusem od ścisłego centrum.
                - 'P&R Lipowa' oraz 'P&R Stadion Miejski': duże parkingi Park & Ride na obrzeżach śródmieścia, świetnie skomunikowane szybkim tramwajem (dojazd do centrum i Starego Miasta zajmuje 6-8 minut).

                Twoje zadanie:
                1. Przeanalizuj cel podróży użytkownika z zapytania i oszacuj odległość w mapach oraz czas dotarcia z dostępnych stref parkingowych.
                2. "Przekmiń" to praktycznie: jeśli użytkownik chce zaparkować w miejscu X (np. Rynek, Galeria, Stare Miasto), a najbliższy parking ma niską szansę na wolne miejsce, szczerze mu to odradź i zaproponuj konkretną alternatywę Y (np. P&R lub sąsiednią strefę), która leży nieco dalej, ale ma wysoką dostępność miejsc i jest świetnie skomunikowana.
                3. Odpowiedz po polsku, na luzie, konkretnie i mega pomocnie w maksymalnie 3-4 zdaniach.
                """.formatted(zonesSummary);

        try {
            log.info("Wysyłanie zapytania do API Google Gemini (model: {}) dla zapytania użytkownika: {}", GEMINI_MODEL, userQuery);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey.trim());

            Map<String, Object> requestBody = Map.of(
                    "systemInstruction", Map.of(
                            "parts", List.of(Map.of("text", systemPrompt))
                    ),
                    "contents", List.of(
                            Map.of(
                                    "role", "user",
                                    "parts", List.of(Map.of("text", userQuery))
                            )
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.7
                    )
            );

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, Map.class);

            return extractContentFromResponse(response.getBody());
        } catch (Exception ex) {
            log.error("Błąd podczas komunikacji z API Google Gemini: {}", ex.getMessage(), ex);
            return "⚠️ Przepraszamy, asystent AI jest chwilowo niedostępny. Sprawdź wskaźniki szans bezpośrednio na liście stref — im wyższy wynik procentowy, tym łatwiej zaparkujesz!";
        }
    }

    private String formatZonesForPrompt(List<ParkingZoneResponse> zones) {
        if (zones.isEmpty()) {
            return "Brak dostępnych stref parkingowych w bazie.";
        }
        return zones.stream()
                .map(zone -> String.format(
                        "- Strefa '%s': pojemność całkowita=%d, typowe obłożenie=%d, aktualnie zaparkowanych użytkowników aplikacji=%d, szansa na miejsce=%d%%",
                        zone.name(),
                        zone.totalCapacity(),
                        zone.typicalOccupancy(),
                        zone.currentAppUsersParked(),
                        zone.probabilityScore()
                ))
                .collect(Collectors.joining("\n"));
    }

    @SuppressWarnings("unchecked")
    private String extractContentFromResponse(Map<String, Object> responseBody) {
        if (responseBody == null || !responseBody.containsKey("candidates")) {
            throw new IllegalStateException("Odpowiedź z API Gemini nie zawiera pola 'candidates'");
        }
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException("Lista 'candidates' w odpowiedzi z API Gemini jest pusta");
        }
        Map<String, Object> firstCandidate = candidates.getFirst();
        Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
        if (content == null || !content.containsKey("parts")) {
            throw new IllegalStateException("Odpowiedź z API Gemini nie zawiera pola 'parts'");
        }
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        if (parts == null || parts.isEmpty() || !parts.getFirst().containsKey("text")) {
            throw new IllegalStateException("Odpowiedź z API Gemini nie zawiera tekstu");
        }
        return String.valueOf(parts.getFirst().get("text")).trim();
    }
}
