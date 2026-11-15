package kazimirowicz.parkingapp.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateParkingZoneRequest(
        @NotBlank(message = "Nazwa strefy parkingowej nie może być pusta")
        String name,

        @NotNull(message = "Całkowita pojemność strefy jest wymagana")
        @Positive(message = "Całkowita pojemność strefy musi być większa od zera")
        Integer totalCapacity,

        @NotNull(message = "Typowe obłożenie strefy jest wymagane")
        @Positive(message = "Typowe obłożenie strefy musi być większe od zera")
        Integer typicalOccupancy,

        @NotNull(message = "Aktualna liczba zaparkowanych użytkowników jest wymagana")
        @Min(value = 0, message = "Aktualna liczba zaparkowanych użytkowników nie może być ujemna")
        Integer currentAppUsersParked
) {
}
