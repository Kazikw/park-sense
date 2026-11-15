package kazimirowicz.parkingapp.mapper;

import kazimirowicz.parkingapp.dto.request.CreateParkingZoneRequest;
import kazimirowicz.parkingapp.dto.response.ParkingZoneResponse;
import kazimirowicz.parkingapp.entity.ParkingZone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParkingZoneMapper {

    @Mapping(target = "id", ignore = true)
    ParkingZone toEntity(CreateParkingZoneRequest request);

    @Mapping(target = "probabilityScore", expression = "java(entity.calculateProbabilityScore())")
    ParkingZoneResponse toResponse(ParkingZone entity);

    List<ParkingZoneResponse> toResponseList(List<ParkingZone> entities);
}
