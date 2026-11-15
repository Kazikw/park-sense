package kazimirowicz.parkingapp.config;

import kazimirowicz.parkingapp.dto.response.ParkingZoneResponse;
import kazimirowicz.parkingapp.entity.ParkingZone;
import kazimirowicz.parkingapp.repository.ParkingZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ParkingZoneRepository parkingZoneRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${server.port:8080}")
    private int serverPort;

    @Override
    public void run(String... args) {
        if (parkingZoneRepository.count() == 0) {
            log.info("Baza danych jest pusta. Inicjalizacja 6 początkowych stref parkingowych...");

            ParkingZone galeriaCentrum = createZone("Galeria Centrum", 150, 120, 110);
            ParkingZone prLipowa = createZone("P&R Lipowa", 200, 140, 20);
            ParkingZone dworzecGlowny = createZone("Dworzec Główny", 100, 80, 45);
            ParkingZone stareMiastoPodwale = createZone("Stare Miasto - Podwale", 60, 50, 48);
            ParkingZone prStadionMiejski = createZone("P&R Stadion Miejski", 300, 220, 35);
            ParkingZone kampusPolitechniki = createZone("Kampus Politechniki", 120, 95, 60);

            parkingZoneRepository.saveAll(List.of(
                    galeriaCentrum,
                    prLipowa,
                    dworzecGlowny,
                    stareMiastoPodwale,
                    prStadionMiejski,
                    kampusPolitechniki
            ));
            log.info("Pomyślnie dodano 6 początkowych stref parkingowych do bazy danych.");
        } else {
            log.info("Baza danych zawiera już strefy parkingowe. Pomijanie inicjalizacji danych.");
        }
    }

    /**
     * Symulator naturalnego ruchu parkingowego (tylko dla danych mockowych).
     * Wysyła standardowe żądania HTTP do API aplikacji tak samo, jak robiłby to klient/system zewnętrzny.
     * Usunięcie klasy DataSeeder całkowicie wyłącza zarówno seedowanie, jak i symulację bez wpływu na resztę aplikacji.
     */
    @Scheduled(initialDelay = 3000, fixedDelay = 3000)
    public void simulateTrafficViaApi() {
        String baseUrl = "http://localhost:" + serverPort + "/api/v1/zones";
        try {
            ResponseEntity<List<ParkingZoneResponse>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            List<ParkingZoneResponse> zones = response.getBody();
            if (zones == null || zones.isEmpty()) {
                return;
            }

            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (ParkingZoneResponse zone : zones) {
                // 60% szans, że w danej strefie w tym cyklu zmieni się liczba zaparkowanych aut
                if (random.nextDouble() < 0.60) {
                    int delta = random.nextInt(-4, 5); // zmiana od -4 do +4 aut
                    if (delta == 0) {
                        continue;
                    }

                    int newCount = Math.clamp(zone.currentAppUsersParked() + delta, 0, zone.totalCapacity());
                    if (newCount != zone.currentAppUsersParked()) {
                        String updateUrl = baseUrl + "/" + zone.id() + "/users?currentAppUsersParked=" + newCount;
                        restTemplate.put(updateUrl, null);
                        log.info("Symulacja ruchu API dla strefy '{}': zmiana zaparkowanych aut z {} na {} (szansa: {}%)",
                                zone.name(), zone.currentAppUsersParked(), newCount, zone.probabilityScore());
                    }
                }
            }
        } catch (Exception ex) {
            log.debug("Pominięto cykl symulacji ruchu API (serwer jeszcze nie nasłuchuje lub jest niedostępny): {}", ex.getMessage());
        }
    }

    private ParkingZone createZone(String name, Integer totalCapacity, Integer typicalOccupancy, Integer currentAppUsersParked) {
        ParkingZone zone = new ParkingZone();
        zone.setName(name);
        zone.setTotalCapacity(totalCapacity);
        zone.setTypicalOccupancy(typicalOccupancy);
        zone.setCurrentAppUsersParked(currentAppUsersParked);
        return zone;
    }
}
