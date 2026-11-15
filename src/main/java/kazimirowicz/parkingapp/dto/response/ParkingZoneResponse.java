package kazimirowicz.parkingapp.dto.response;

public record ParkingZoneResponse(
        Long id,
        String name,
        Integer totalCapacity,
        Integer typicalOccupancy,
        Integer currentAppUsersParked,
        int probabilityScore
) {
}
