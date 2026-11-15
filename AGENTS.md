# AI Agent Constitution: Park-Sense MVP (Recruitment Task)

## 1. Kontekst Projektu i Cel
Tworzymy pełnostosową aplikację "Park-Sense" (MVP w 24 godziny) rozwiązującą problem szukania miejsc parkingowych.
Projekt zakłada predykcję szans na znalezienie miejsca na podstawie stref (ParkingZone), z wykorzystaniem asystenta AI tłumaczącego dane na ludzki język.
Najważniejsza jest jakość kodu, czysta architektura (Clean Code) i udowodnienie znajomości dobrych praktyk.

## 2. Ograniczenia i Wymagania Architektoniczne
- **Repozytorium:** Główny folder musi zawierać dwa podfoldery: `/backend` i `/frontend`.
- **Baza danych:** Używamy TYLKO in-memory H2 (z włączoną konsolą w application.yml i automatycznym generowaniem schematu DDL).
- **CORS:** Globalna konfiguracja CORS w backendzie odblokowująca zapytania z `http://localhost:3000` oraz `http://localhost:5173` (vite).
- **Brak Spring Security:** Ze względu na 24h limit czasu na MVP, API jest otwarte. Skupiamy się na logice domenowej i AI.

## 3. Stack Technologiczny Backend
- Java 21 + Spring Boot 3.x
- Spring Data JPA + H2 Database
- Lombok (bardzo restrykcyjnie) + MapStruct (do mapowania)
- Integracja z zewnętrznym API LLM (np. Gemini / OpenAI) przez prosty `RestTemplate` lub `RestClient`.

## 4. Mój Styl Kodowania (Narzucany dla Agenta)
Musisz pisać kod dokładnie w moim stylu. Oto zasady:

### Struktura pakietów (Horyzontalna)
- `config`, `controller`, `dto.request`, `dto.response`, `entity`, `exception`, `mapper`, `repository`, `service`.

### Kontrolery (Thin Controllers + REST v1)
- Używaj `@RestController` i `@RequestMapping("/api/v1/<zasob>")`.
- Kontroler ma tylko walidować wejście (`@Valid @RequestBody`) i delegować do serwisu.
- Zwracaj `ResponseEntity`: `.status(HttpStatus.CREATED)` dla POST, `.noContent()` dla DELETE, a dla GET po prostu listę lub pojedynczy `...Response`.

### Wstrzykiwanie zależności i Serwisy
- Wstrzykuj zależności WYŁĄCZNIE przez konstruktor: używaj Lombok `@RequiredArgsConstructor` i pól `private final`. ZAKAZ używania `@Autowired` na polach.
- Metody w serwisach mutujące dane muszą mieć `@Transactional`.
- Stosuj styl **fail-fast** (guard clauses): na początku metody pobieraj encje (używając `.orElseThrow(...)`), wykonuj walidacje (if ... throw) i dopiero potem procesuj logikę.

### Encje JPA (Rich Domain Model)
- Używaj `@Entity`, `@Getter`, `@Setter` oraz `@FieldDefaults(level = AccessLevel.PRIVATE)`.
- ZAKAZ używania `@Data` na encjach JPA.
- Klucz główny to `Long id` z `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
- Prosta logika sprawdzająca stan (np. `isFull()`, `hasHighProbability()`) powinna znajdować się w klasie encji.

### DTO jako Java Records
- Wszystkie wejścia/wyjścia to `record` umieszczony w `dto.request` lub `dto.response`.
- Walidacja (Bean Validation: `@NotNull`, `@NotBlank`, itp.) ląduje w rekordach. Komunikaty walidacji mają być po polsku.

### MapStruct
- Konwersja Encja <-> DTO zawsze przez interfejs w pakiecie `mapper` z `@Mapper(componentModel = "spring")`.

### Obsługa Błędów
- ZAKAZ rzucania ogólnych wyjątków. Twórz granularne wyjątki (np. `ZoneNotFoundException`) dziedziczące po `RuntimeException`.
- Używaj centralnego `GlobalExceptionHandler` (`@RestControllerAdvice`), który łapie wyjątki domenowe i zwraca estetyczny obiekt `ErrorResponse` z odpowiednim kodem HTTP (np. 404, 400).

### Język i Logowanie
- Cały kod: nazwy klas, zmiennych, metod - **po angielsku**.
- Wszystkie komunikaty dla użytkownika, wiadomości w wyjątkach, logi i komentarze - **po polsku**.
- Do logowania używaj `@Slf4j` i placeholderów `{}`.