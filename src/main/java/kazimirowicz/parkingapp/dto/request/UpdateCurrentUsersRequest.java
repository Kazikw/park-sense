package kazimirowicz.parkingapp.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCurrentUsersRequest(
        @NotNull(message = "Aktualna liczba zaparkowanych użytkowników jest wymagana")
        @Min(value = 0, message = "Aktualna liczba zaparkowanych użytkowników nie może być ujemna")
        Integer currentAppUsersParked
) {
}
