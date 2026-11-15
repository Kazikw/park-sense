package kazimirowicz.parkingapp.service;

import kazimirowicz.parkingapp.dto.request.CreateParkingZoneRequest;
import kazimirowicz.parkingapp.dto.response.ParkingZoneResponse;
import kazimirowicz.parkingapp.entity.ParkingZone;
import kazimirowicz.parkingapp.exception.InvalidZoneDataException;
import kazimirowicz.parkingapp.exception.ZoneNotFoundException;
import kazimirowicz.parkingapp.mapper.ParkingZoneMapper;
import kazimirowicz.parkingapp.repository.ParkingZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingZoneService {

    private final ParkingZoneRepository parkingZoneRepository;
    private final ParkingZoneMapper parkingZoneMapper;

    @Transactional(readOnly = true)
    public List<ParkingZoneResponse> getAllZones() {
        log.info("Pobieranie wszystkich stref parkingowych");
        List<ParkingZone> zones = parkingZoneRepository.findAll();
        return parkingZoneMapper.toResponseList(zones);
    }

    @Transactional
    public ParkingZoneResponse addZone(CreateParkingZoneRequest request) {
        if (request.typicalOccupancy() > request.totalCapacity()) {
            throw new InvalidZoneDataException("Typowe obłożenie nie może przekraczać całkowitej pojemności strefy");
        }
        if (request.currentAppUsersParked() > request.totalCapacity()) {
            throw new InvalidZoneDataException("Aktualna liczba zaparkowanych użytkowników nie może przekraczać całkowitej pojemności strefy");
        }

        log.info("Dodawanie nowej strefy parkingowej: {}", request.name());
        ParkingZone zone = parkingZoneMapper.toEntity(request);
        ParkingZone savedZone = parkingZoneRepository.save(zone);
        log.info("Pomyślnie utworzono strefę parkingową o ID: {}", savedZone.getId());
        return parkingZoneMapper.toResponse(savedZone);
    }

    @Transactional
    public ParkingZoneResponse updateCurrentUsers(Long zoneId, Integer currentAppUsersParked) {
        ParkingZone zone = parkingZoneRepository.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException(zoneId));

        if (currentAppUsersParked == null || currentAppUsersParked < 0) {
            throw new InvalidZoneDataException("Aktualna liczba zaparkowanych użytkowników nie może być ujemna");
        }
        if (currentAppUsersParked > zone.getTotalCapacity()) {
            throw new InvalidZoneDataException("Aktualna liczba zaparkowanych użytkowników nie może przekraczać całkowitej pojemności strefy");
        }

        log.info("Aktualizacja liczby użytkowników w strefie o ID: {} na wartość: {}", zoneId, currentAppUsersParked);
        zone.setCurrentAppUsersParked(currentAppUsersParked);
        ParkingZone updatedZone = parkingZoneRepository.save(zone);
        return parkingZoneMapper.toResponse(updatedZone);
    }
}
