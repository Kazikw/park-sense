#  Park-Sense (MVP) - Xelerit Recruitment Task

**Park-Sense** to fullstackowa aplikacja webowa rozwiązująca codzienny problem kierowców polegający na znalezieniu wolnego miejsca parkingowego. Projekt powstał jako MVP w ramach zadania rekrutacyjnego dla firmy Xelerit.

##  Funkcjonalności (Creative Twist)
* **Live Dashboard:** Podgląd zajętości stref parkingowych z dynamicznym wskaźnikiem procentowym szans na zaparkowanie.
* **Symulator Ruchu (DataSeeder):** Aplikacja "żyje" – specjalny moduł w tle symuluje naturalny ruch pojazdów, aktualizując dane w czasie rzeczywistym.
* **Doradca AI (Creative Twist):** Zintegrowany model Google Gemini 3.5 Flash pełni rolę asystenta. Analizuje aktualne obłożenie parkingów, odległości oraz natężenie ruchu, a następnie doradza kierowcy najlepszą alternatywę w ludzkim, przystępnym języku. Posiada wbudowany *Safe Mode* – jeśli nie podasz klucza API, system zwróci bezpieczną, mockowaną odpowiedź.

##  Stos technologiczny
* **Backend:** Java 21, Spring Boot 4.1.2, Spring Data JPA
* **Baza danych:** H2 (In-Memory) z automatycznym DDL
* **Frontend:** React 18, Vite, Tailwind CSS, Axios
* **AI:** Integracja z Google Gemini API (przez RestTemplate)

Czysta architektura: *Thin Controllers*, wstrzykiwanie zależności przez konstruktory, pattern *fail-fast*, globalna obsługa wyjątków domenowych oraz mapowanie DTO (rekordy) za pomocą MapStruct.

## Jak uruchomić projekt lokalnie?

### Wymagania
* Java 21+
* Node.js 18+

### 1. Backend (Spring Boot)

1. Będąc w głównym katalogu projektu, opcjonalnie ustaw klucz API Google Gemini (aby uaktywnić Asystenta AI na żywo):

   ```bash
   export GEMINI_API_KEY="twój_klucz_api"
   ```

2. Uruchom aplikację za pomocą Maven Wrappera:

   ```bash
   ./mvnw spring-boot:run
   ```

   Na systemie Windows:

   ```cmd
   mvnw.cmd spring-boot:run
   ```

3. Backend wystartuje pod adresem: http://localhost:8080

4. Konsola bazy H2 dostępna jest pod adresem: http://localhost:8080/h2-console

   **Dane logowania:**
   * JDBC URL: `jdbc:h2:mem:parksensedb`
   * Użytkownik: `sa`
   * Hasło: brak

### 2. Frontend (React + Vite)
1. W nowym oknie terminala przejdź do katalogu frontend:

   ```bash
   cd frontend
   ```
2. Zainstaluj zależności:

   ```bash
   npm install
   ```
3. Uruchom serwer deweloperski:

   ```bash
   npm run dev
   ```
4. Otwórz aplikację w przeglądarce pod adresem: http://localhost:5173

##  Wsparcie narzędziami AI
Przy pracy nad tym kodem wspierałem się AI (LLM / IDE z asystentem). 
- Skonfigurowałem dedykowany zestaw ścisłych reguł w pliku `AGENTS.md` (załączonym w repozytorium), co wymusiło na AI stosowanie spójnego ze mną stylu kodowania (*Clean Code*, anglojęzyczne nazewnictwo zmiennych, polskojęzyczne komunikaty dla użytkownika, struktura rekordów DTO).
- AI pomogło mi wygenerować boilerplate dla Spring Boota oraz ostylować komponenty Reacta w Tailwind CSS.
- Wykorzystałem agenta jako narzędzie audytowe przed commitem (skanowanie kodu w poszukiwaniu ukrytych śmieci debugowych i wycieków zmiennych środowiskowych – tzw. *security check* w trybie read-only).

## 🎥 Demo aplikacji
Odtwarzacz GitHuba ma problemy z buforowaniem większych plików wideo. Aby ułatwić przegląd zadania, nagranie prezentujące działanie aplikacji na żywo umieściłem także na youtube:
https://youtu.be/oF6UDVN2DSs

