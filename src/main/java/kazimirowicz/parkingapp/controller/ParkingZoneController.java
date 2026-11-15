package kazimirowicz.parkingapp.controller;

import jakarta.validation.Valid;
import kazimirowicz.parkingapp.dto.request.CreateParkingZoneRequest;
import kazimirowicz.parkingapp.dto.request.UpdateCurrentUsersRequest;
import kazimirowicz.parkingapp.dto.response.ParkingZoneResponse;
import kazimirowicz.parkingapp.service.ParkingZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/zones")
@RequiredArgsConstructor
public class ParkingZoneController {

    private final ParkingZoneService parkingZoneService;

    @GetMapping
    public ResponseEntity<List<ParkingZoneResponse>> getAllZones() {
        return ResponseEntity.ok(parkingZoneService.getAllZones());
    }

    @PostMapping
    public ResponseEntity<ParkingZoneResponse> addZone(@Valid @RequestBody CreateParkingZoneRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingZoneService.addZone(request));
    }

    @PatchMapping("/{id}/users")
    public ResponseEntity<ParkingZoneResponse> updateCurrentUsers(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCurrentUsersRequest request
    ) {
        return ResponseEntity.ok(parkingZoneService.updateCurrentUsers(id, request.currentAppUsersParked()));
    }

    @PutMapping("/{id}/users")
    public ResponseEntity<ParkingZoneResponse> updateCurrentUsersByParam(
            @PathVariable Long id,
            @RequestParam Integer currentAppUsersParked
    ) {
        return ResponseEntity.ok(parkingZoneService.updateCurrentUsers(id, currentAppUsersParked));
    }
}
